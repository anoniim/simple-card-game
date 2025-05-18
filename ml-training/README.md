# ML training of AI opponents

## Pre-requisites
- Python 3.6 or higher
- pip3
- virtualenv

## Setup
### Activate the virtual environment with 

```source venv/bin/activate```

### Install the required Python packages

```pip3 install -r requirements.txt```

> **NOTE**  
> This is important to do before building the project because installing py4j python library provides the necessary JAR dependencies for the Kotlin code to run.

## Training the AI

### Run the Kotlin code

Either run the `src/main/kotlin/MainKt.main()` function in an IDE (like IntelliJ IDEA) or build the project with Gradle and run the JAR file.

```bash
./gradlew ml-training:jar
java -jar ml-training/build/libs/ml-training-1-all.jar
```

### Run the training script

```python3 train.py```

## Neural network architecture

### Input layer

The input layer is an int array with 13 elements representing the state of the game. The elements are as follows:

1. Index of the starting player in the current round 
2. Point value of the currently drawn card 
3. Number of points missing to reach goal
4. Number of coins
5. Opponent #1 - Number of points missing to reach goal
6. Opponent #1 - Number of coins 
7. Opponent #1 - Current bid 
8. Opponent #2 - Number of points missing to reach goal
9. Opponent #2 - Number of coins 
10. Opponent #2 - Current bid 
11. Opponent #3 - Number of points missing to reach goal
12. Opponent #3 - Number of coins 
13. Opponent #3 - Current bid 

Current bid is represented as follows:
* -1 if not yet bid
* 0 if passed
* 1+ number of coins bid

### Action space

The action space is a 1D array with variable number of elements representing the possible actions the AI can take. The elements are as follows:

1. Pass
2. Bid min coins (previous bid + 1)
3. Bid min + 1 coin
4. Bid min + 2 coins
5. ... (up to the number of coins the AI has)

### Future improvements

* Feature engineering
  * Include bid history in the state
  * Add the number of cards in the deck, the number of cards in the discard pile, what cards have been played, etc.
* Network Architecture
  * Use a multi-layer perceptron (MLP) with separate input layers for different features
  * Use embedded layers for categorical features like the starting player index
  * Use a Recurrent Neural Network (like LSTM or GRU) to capture the sequential nature of the game and notice patterns and dependencies across multiple rounds
* Activation functions
  * Use ReLU activation functions for hidden layers and a softmax activation function for the output layer
* Regularization
  * Use the categorical cross-entropy loss function