import sys

import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers, losses, optimizers
import numpy as np
import random

from py4j.java_gateway import JavaGateway
from py4j.protocol import Py4JNetworkError


# Initialize the environment
gateway = JavaGateway() # connect to the JVM
env = gateway.entry_point # get the TrainingEnvironment instance
try:
    print("Hi hello! Let's train the model", env.getModelName())
except Py4JNetworkError as e:
    print("Looks like Java/Kotlin program is not running yet!")
    print(e)
    sys.exit(1)


# Define state and action space
state_size = env.getGameStateArraySize()
action_size = env.getActionSpaceSize()


# Build DNQ model
model = keras.Sequential([
    layers.Input(shape=(state_size,)),  # Input layer with state size
    layers.Masking(mask_value=-1),  # Masking layer to handle variable-length input
    layers.Dense(64, activation='relu'),
    layers.Dense(64, activation='relu'),
    # layers.LSTM(32), # Experiment with LSTM layer (hidden state that summarizes the information from prevous rounds)
    layers.Dense(action_size)  # Output layer with number of actions
])


# Set decaying epsilon-greedy parameters
model.compile(loss=losses.MeanSquaredError(), optimizer=optimizers.Adam())
epsilon = 1.0 # Initial epsilon
epsilon_min = 0.01 # Minimum epsilon
epsilon_decay = 0.995 # Decay rate


# Set up replay memory and target model
replay_memory = []
batch_size = 6
target_model = keras.models.clone_model(model)
target_model.set_weights(model.get_weights())
target_update_frequency = 20
gamma = 0.95  # Discount factor (for future rewards)


# Training loop
num_episodes = 20
for episode in range(num_episodes):
    state = np.array(env.reset(), dtype=np.int32)  # Convert state to NumPy array
    done = False

    while not done:
        # Choose action (e.g., epsilon-greedy)
        actions = np.array(env.getActions(), dtype=np.int32)
        action_mask = (actions != -1).astype(np.float32)  # Mask for valid actions

        # Epsilon-greedy policy can't be in use until masking works as expected
        if random.random() < epsilon:
            action = np.random.choice(actions[action_mask == 1]) # Exploration
        else:
            # Apply mask to Q-values before argmax for exploitation
            q_values = model.predict(state.reshape(1, -1))
            masked_q_values = q_values * action_mask
            action = np.argmax(masked_q_values)

        # Take action and observe
        next_state, reward, done = env.step(int(action))
        next_state = np.array(next_state, dtype=np.int32)

        # Store experience
        replay_memory.append((state, action, reward, next_state, done))

        # Sample from replay memory and train model
        if len(replay_memory) > batch_size:
            minibatch = random.sample(replay_memory, batch_size)
            states, actions, rewards, next_states, dones = zip(*minibatch)

            # Calculate target Q-values using the target model
            target_qs = (rewards + gamma * np.amax(target_model.predict(np.array(next_states)), axis=1) * (1 - np.array(dones))).reshape(-1, 1)

            # Apply masking to the target Q-values for invalid actions
            valid_actions_batch = [env.getActions() for _ in range(batch_size)]  
            masks = np.array([np.isin(np.arange(action_size), valid_actions) for valid_actions in valid_actions_batch])
            target_qs = target_qs * masks
            
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
model.save("card_game_dqn_model.keras")