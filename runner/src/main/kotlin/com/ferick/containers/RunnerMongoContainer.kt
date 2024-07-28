package com.ferick.containers

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network

class RunnerMongoContainer(
    network: Network,
    val username: String = USERNAME,
    val password: String = PASSWORD
) : GenericContainer<RunnerMongoContainer>(IMAGE), ContainerUrl {

    init {
        withNetwork(network)
        withNetworkAliases(NETWORK_ALIAS)
    }

    override fun getLocalUrl(): String = "mongodb://$username:$password@$host:$firstMappedPort"

    override fun getDockerNetworkUrl(): String = "mongodb://$username:$password@$NETWORK_ALIAS:$PORT"

    override fun configure() {
        addEnv("MONGO_INITDB_ROOT_USERNAME", username)
        addEnv("MONGO_INITDB_ROOT_PASSWORD", password)
    }

    companion object {
        private const val IMAGE = "mongo:4.0.10"
        const val NETWORK_ALIAS = "mongodb"
        const val PORT = 27017
        const val USERNAME = "admin"
        const val PASSWORD = "password"
    }
}
