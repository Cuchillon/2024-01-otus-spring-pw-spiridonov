package com.ferick.converters

import com.fasterxml.jackson.databind.ObjectMapper
import com.ferick.common.model.AggregatedSiteData
import org.apache.kafka.common.serialization.Deserializer
import org.springframework.stereotype.Component

@Component
class AggregatedSiteDataDeserializer(
    private val objectMapper: ObjectMapper
) : Deserializer<AggregatedSiteData> {

    override fun deserialize(topic: String, data: ByteArray): AggregatedSiteData =
        objectMapper.readValue(data, AggregatedSiteData::class.java)
}
