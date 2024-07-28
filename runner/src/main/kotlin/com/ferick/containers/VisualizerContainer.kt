package com.ferick.containers

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import java.util.concurrent.Future

class VisualizerContainer(
    image: Future<String>,
    network: Network,
    kafkaContainer: RunnerKafkaContainer,
    mongoContainer: RunnerMongoContainer
) : GenericContainer<VisualizerContainer>(image), ContainerUrl {

    init {
        withNetwork(network)
        withNetworkAliases(NETWORK_ALIAS)
        dependsOn(mongoContainer, kafkaContainer)
    }

    override fun getLocalUrl(): String = "http://$host:$firstMappedPort"

    override fun getDockerNetworkUrl(): String = "http://$NETWORK_ALIAS:$PORT"

    companion object {
        const val NETWORK_ALIAS = "visualizer"
        const val PORT = 8080
    }
}
