package com.ferick.containers

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

class RunnerKafkaContainer(
    network: Network,
    runnerZookeeperContainer: RunnerZookeeperContainer
) : GenericContainer<RunnerKafkaContainer>(DockerImageName.parse(IMAGE)), ContainerUrl {

    init {
        withNetwork(network)
        withNetworkAliases(NETWORK_ALIAS)
        withExposedPorts(PORT)
        waitingFor(Wait.forLogMessage(".*\\[KafkaServer id=\\d+\\] started.*", 1))
        dependsOn(runnerZookeeperContainer)
        withEnv(
            mapOf(
                "KAFKA_ZOOKEEPER_CONNECT" to runnerZookeeperContainer.getDockerNetworkUrl(),
                "KAFKA_LISTENERS" to "EXTERNAL_SAME_HOST://:29092,INTERNAL://:9092",
                "KAFKA_ADVERTISED_LISTENERS" to "INTERNAL://kafka:9092,EXTERNAL_SAME_HOST://localhost:29092",
                "KAFKA_LISTENER_SECURITY_PROTOCOL_MAP" to "INTERNAL:PLAINTEXT,EXTERNAL_SAME_HOST:PLAINTEXT",
                "KAFKA_INTER_BROKER_LISTENER_NAME" to "INTERNAL",
                "KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR" to "1"
            )
        )
    }

    override fun getLocalUrl(): String = "$host:$firstMappedPort"

    override fun getDockerNetworkUrl(): String = "$NETWORK_ALIAS:$INTERNAL_PORT"

    companion object {
        private const val IMAGE = "confluentinc/cp-kafka:latest"
        const val NETWORK_ALIAS = "kafka"
        const val PORT = 29092
        const val INTERNAL_PORT = 9092
    }
}
