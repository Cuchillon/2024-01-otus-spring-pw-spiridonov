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
        withExposedPorts(PORT)
        dependsOn(mongoContainer, kafkaContainer)
        withEnv(
            mapOf(
                "SPRING_KAFKA_BOOTSTRAP_SERVERS" to kafkaContainer.getDockerNetworkUrl(),
                "SPRING_DATA_MONGODB_HOST" to RunnerMongoContainer.NETWORK_ALIAS
            )
        )
    }

    override fun getLocalUrl(): String = "http://$host:$firstMappedPort"

    override fun getDockerNetworkUrl(): String = "http://$NETWORK_ALIAS:$PORT"

    companion object {
        const val NETWORK_ALIAS = "visualizer"
        const val PORT = 8080
    }
}
