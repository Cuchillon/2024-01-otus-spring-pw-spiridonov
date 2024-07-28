package com.ferick.configuration.containers

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.Network

@Configuration
class ContainerNetworkConfiguration {

    @Bean
    fun network(): Network = Network.newNetwork()
}
