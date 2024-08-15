package com.ferick.configuration.clients

import com.ferick.containers.ServiceContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {

    @Bean
    fun webClient(receiver: ServiceContainer): WebClient =
        WebClient.builder()
            .baseUrl(receiver.getLocalUrl())
            .build()
}
