import sys

import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers, losses, optimizers
import numpy as np
import random

from py4j.java_gateway import JavaGateway
from py4j.protocol import Py4JNetworkError

INVALID_ACTION = -1
enable_logs = False

# Initialize the environment
gateway = JavaGateway() # connect to the JVM
env = gateway.entry_point # get the TrainingEnvironment instance
try:
    print("Hi hello! Let's train the model", env.getModelName())
except Py4JNetworkError as e:
    print("Looks like Java/Kotlin program is not running yet!")
    print(e)
    sys.exit(1)


model_name = env.getModelName()
win_count = 0


# Define state and action space
state_size = env.getGameStateArraySize()
action_size = env.getActionSpaceSize()


# Build DNQ model
model = keras.Sequential([
    layers.Input(shape=(state_size,)),  # Input layer with state size
    # layers.Masking(mask_value=-1),  # Masking layer to handle variable-length input
    layers.Dense(64, activation='relu'),
    layers.Dense(64, activation='relu'),
    # layers.LSTM(32), # Experiment with LSTM layer (hidden state that summarizes the information from previous rounds)
    layers.Dense(action_size)  # Output layer with number of actions
])


# Set decaying epsilon-greedy parameters
model.compile(loss=losses.MeanSquaredError(), optimizer=optimizers.Adam())
epsilon = 1.0 # Initial epsilon
epsilon_min = 0.01 # Minimum epsilon
epsilon_decay = 0.995 # Decay rate


# Set up replay memory and target model
replay_memory = []
batch_size = 16
target_model = keras.models.clone_model(model)
target_model.set_weights(model.get_weights())
target_update_frequency = 20
gamma = 0.95  # Discount factor (for future rewards)


# Training loop
num_episodes = 100
for episode in range(num_episodes):
    if (enable_logs): print(f"Episode {episode + 1}/{num_episodes}")
    # Start a new game
    state = np.array(env.reset(), dtype=np.int32)  # Convert state to NumPy array
    done = False

    while not done:
        # Choose action (using epsilon-greedy strategy)
        actions = np.array(env.getActions(), dtype=np.int32)
        if (enable_logs): print("Actions: ", actions)
        action_mask = (actions != INVALID_ACTION)  # Mask for valid actions
        if (enable_logs): print("Action mask: ", action_mask)
        valid_actions = actions[action_mask]
        if valid_actions.size == 0:
            action = env.ACTION_PASS
            if (enable_logs): print("No valid actions, passing turn")
        else:
            if random.random() < epsilon:
                # Exploration
                action = np.random.choice(valid_actions)
                if (enable_logs): print("Exploration action: ", action)
            else:
                # Exploitation
                q_values = model.predict(state.reshape(1, -1)) # DQN expects a batch of inputs (hence reshaped to a 2D array with 1 row)
                if (enable_logs): print("Q-values: ", q_values)
                # Apply mask to Q-values before argmax for exploitation
                q_values[0][~action_mask] = -np.inf
                masked_q_values = q_values[0]
                if (enable_logs): print("Masked Q-values: ", masked_q_values)
                action = actions[np.argmax(masked_q_values)]
                if (enable_logs): print("Exploitation action: ", action)

        # Take action and observe
        next_state, reward, done = env.step(int(action))
        next_state = np.array(next_state, dtype=np.int32)

        if done:
            if reward > 0:
                win_count += 1
                print(model_name, " WON the game!")
            else: print("Game lost")

        # Store experience
        replay_memory.append((state, action, reward, next_state, done))

        # Update state for the next round
        state = next_state

        # Sample from replay memory and train model
        if len(replay_memory) > batch_size:
            minibatch = random.sample(replay_memory, batch_size)
            states, actions, rewards, next_states, dones = zip(*minibatch)

            # Calculate target Q-values using the target model
            target_qs = (rewards + gamma * np.amax(target_model.predict(np.array(next_states)), axis=1) * (1 - np.array(dones))).reshape(-1, 1)

            # Apply masking to the target Q-values for invalid actions (setting them to -inf)
            valid_actions_batch = [list(filter(lambda a: a != INVALID_ACTION, env.getActions())) for _ in range(batch_size)]
            masks = np.array([np.isin(np.arange(action_size), valid_actions) for valid_actions in valid_actions_batch])
            inf_matrix = np.full_like(target_qs, -np.inf)
            target_qs = np.where(masks, target_qs, inf_matrix)

            target_f = model.predict(np.array(states))
            for i, action in enumerate(actions):
                target_f[i][action] = target_qs[i][0]  # Set target for the chosen action

            model.fit(np.array(states), target_f, epochs=1, verbose=0)

        # Update target network
        if episode % target_update_frequency == 0:
            target_model.set_weights(model.get_weights())

        # Decay epsilon after each episode:
        epsilon = max(epsilon * epsilon_decay, epsilon_min)


# Save the trained model
print("Training finished, won ", win_count, "/", num_episodes, " games")
model.save(f"card_game_dqn_model_{model_name}.keras")