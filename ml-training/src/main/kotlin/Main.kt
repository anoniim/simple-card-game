package net.solvetheriddle.cardgame

import py4j.GatewayServer


fun main() {
    val gatewayServer = GatewayServer(TrainingEnvironment())
    gatewayServer.start()
    println("Gateway Server Started")
}