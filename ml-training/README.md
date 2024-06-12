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