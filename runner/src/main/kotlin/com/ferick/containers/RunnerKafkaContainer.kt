package com.ferick.containers

import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.utility.DockerImageName

class RunnerKafkaContainer(
    network: Network
) : KafkaContainer(DockerImageName.parse(IMAGE)), ContainerUrl {

    init {
        withNetwork(network)
        withNetworkAliases(NETWORK_ALIAS)
    }

    override fun getLocalUrl(): String = bootstrapServers

    override fun getDockerNetworkUrl(): String = "PLAINTEXT://$NETWORK_ALIAS:$KAFKA_PORT"

    companion object {
        private const val IMAGE = "confluentinc/cp-kafka:6.2.1"
        const val NETWORK_ALIAS = "kafka"
    }
}
