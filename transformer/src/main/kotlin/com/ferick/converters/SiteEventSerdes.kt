package com.ferick.converters

import com.fasterxml.jackson.databind.ObjectMapper
import com.ferick.common.model.SiteEvent
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serdes.WrapperSerde
import org.apache.kafka.common.serialization.Serializer
import org.springframework.stereotype.Component

@Component
class SiteEventSerializer(
    private val objectMapper: ObjectMapper
) : Serializer<SiteEvent> {

    override fun serialize(topic: String, data: SiteEvent): ByteArray =
        objectMapper.writeValueAsBytes(data)
}

@Component
class SiteEventDeserializer(
    private val objectMapper: ObjectMapper
) : Deserializer<SiteEvent> {

    override fun deserialize(topic: String, data: ByteArray): SiteEvent =
        objectMapper.readValue(data, SiteEvent::class.java)
}

@Component
class SiteEventSerde(
    siteEventSerializer: SiteEventSerializer,
    siteEventDeserializer: SiteEventDeserializer
) : WrapperSerde<SiteEvent>(
    siteEventSerializer, siteEventDeserializer
)
