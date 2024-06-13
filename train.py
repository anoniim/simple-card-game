import sys
from py4j.java_gateway import JavaGateway
from py4j.protocol import Py4JNetworkError

gateway = JavaGateway() # connect to the JVM
env = gateway.entry_point # get the TrainingEnvironment instance

try:
    print("Hi hello! Let's train the model", env.getModelName())
except Py4JNetworkError as e:
    print("Looks like Java/Kotlin program is not running yet!")
    print(e)
    sys.exit(1)


