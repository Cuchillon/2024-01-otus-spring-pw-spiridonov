package com.ferick.containers

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import java.util.concurrent.Future

class ServiceContainer(
    image: Future<String>,
    network: Network,
    val networkAlias: String,
    kafkaContainer: RunnerKafkaContainer
) : GenericContainer<ServiceContainer>(image), ContainerUrl {

    init {
        withNetwork(network)
        withNetworkAliases(networkAlias)
        withExposedPorts(PORT)
        dependsOn(kafkaContainer)
        withEnv(
            mapOf(
                "SPRING_KAFKA_BOOTSTRAP_SERVERS" to kafkaContainer.getDockerNetworkUrl()
            )
        )
    }

    override fun getLocalUrl(): String = "http://$host:$firstMappedPort"

    override fun getDockerNetworkUrl(): String = "http://$networkAlias:$PORT"

    companion object {
        const val PORT = 8080
    }
}
