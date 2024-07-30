package com.ferick.converters

import com.fasterxml.jackson.databind.ObjectMapper
import com.ferick.common.model.SiteEvent
import org.apache.kafka.common.serialization.Serializer
import org.springframework.stereotype.Component

@Component
class SiteEventSerializer(
    private val objectMapper: ObjectMapper
) : Serializer<SiteEvent> {
    override fun serialize(topic: String, data: SiteEvent): ByteArray = objectMapper.writeValueAsBytes(data)
}
