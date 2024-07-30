package com.ferick.service.impl

import com.ferick.common.model.SiteEvent
import com.ferick.configuration.properties.TargetProperties
import com.ferick.model.SiteEventDto
import com.ferick.service.SiteEventService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord

@Service
class SiteEventServiceImpl(
    private val targetProperties: TargetProperties,
    private val kafkaSender: KafkaSender<String, SiteEvent>
) : SiteEventService {

    override fun sendEvent(event: SiteEventDto): Mono<SiteEvent> {
        val siteEvent = SiteEvent(
            dateTime = event.dateTime,
            userId = event.userId,
            page = event.page,
            period = event.period
        )
        val record = Mono.just(
            SenderRecord.create(
                targetProperties.topicName, null, null, event.site, siteEvent, siteEvent
            )
        )
        return kafkaSender.send(record).next().map { it.correlationMetadata() }
    }
}
