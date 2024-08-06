package com.ferick.containers

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.utility.DockerImageName

class RunnerZookeeperContainer(
    network: Network
) : GenericContainer<RunnerZookeeperContainer>(DockerImageName.parse(IMAGE)), ContainerUrl {

    init {
        withNetwork(network)
        withNetworkAliases(NETWORK_ALIAS)
        withExposedPorts(PORT)
        withEnv(
            mapOf(
                "ZOOKEEPER_CLIENT_PORT" to PORT.toString(),
                "ZOOKEEPER_TICK_TIME" to "2000"
            )
        )
    }

    override fun getLocalUrl(): String = "$host:$firstMappedPort"

    override fun getDockerNetworkUrl(): String = "$NETWORK_ALIAS:$PORT"

    companion object {
        private const val IMAGE = "confluentinc/cp-zookeeper:latest"
        const val NETWORK_ALIAS = "zookeeper"
        const val PORT = 2181
    }
}
