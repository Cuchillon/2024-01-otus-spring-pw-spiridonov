package com.ferick.configuration.containers

import com.ferick.containers.RunnerKafkaContainer
import com.ferick.containers.RunnerMongoContainer
import com.ferick.containers.RunnerZookeeperContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.Network

@Configuration
class SidecarConfiguration(
    private val network: Network
) {

    @Bean
    fun zookeeper(): RunnerZookeeperContainer = RunnerZookeeperContainer(network).also { it.start() }

    @Bean
    fun kafka(zookeeper: RunnerZookeeperContainer): RunnerKafkaContainer =
        RunnerKafkaContainer(network, zookeeper).also { it.start() }

    @Bean
    fun mongo(): RunnerMongoContainer = RunnerMongoContainer(network).also { it.start() }
}
