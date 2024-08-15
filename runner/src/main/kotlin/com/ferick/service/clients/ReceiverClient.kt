package com.ferick.service.clients

import com.ferick.common.model.SiteEventDto
import com.ferick.helpers.SiteEventGenerator
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@Component
class ReceiverClient(
    private val webClient: WebClient,
    private val siteEventGenerator: SiteEventGenerator
) : InitializingBean {

    override fun afterPropertiesSet() {
        val events = siteEventGenerator.generate(LocalDate.now().minusDays(1))
        events.forEach {
            sendEvent(it).subscribe()
            TimeUnit.MILLISECONDS.sleep(10)
        }
    }

    fun sendEvent(event: SiteEventDto): Mono<String> {
        return webClient.post()
            .uri("/site-event")
            .bodyValue(event)
            .retrieve()
            .bodyToMono(String::class.java)
    }
}
