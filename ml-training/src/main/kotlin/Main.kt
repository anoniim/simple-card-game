package net.solvetheriddle.cardgame

import py4j.GatewayServer


fun main() {
    val gatewayServer = GatewayServer(TrainingEntryPoint())
    gatewayServer.start()
    println("Gateway Server Started")
}