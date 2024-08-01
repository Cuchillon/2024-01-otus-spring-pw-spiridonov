package com.ferick.converters

import com.fasterxml.jackson.databind.ObjectMapper
import com.ferick.common.model.AggregatedSiteData
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serdes.WrapperSerde
import org.apache.kafka.common.serialization.Serializer
import org.springframework.stereotype.Component

@Component
class AggregatedSiteDataSerializer(
    private val objectMapper: ObjectMapper
) : Serializer<AggregatedSiteData> {

    override fun serialize(topic: String, data: AggregatedSiteData): ByteArray =
        objectMapper.writeValueAsBytes(data)
}

@Component
class AggregatedSiteDataDeserializer(
    private val objectMapper: ObjectMapper
) : Deserializer<AggregatedSiteData> {

    override fun deserialize(topic: String, data: ByteArray): AggregatedSiteData =
        objectMapper.readValue(data, AggregatedSiteData::class.java)
}

@Component
class AggregatedSiteDataSerde(
    aggregatedSiteDataSerializer: AggregatedSiteDataSerializer,
    aggregatedSiteDataDeserializer: AggregatedSiteDataDeserializer
) : WrapperSerde<AggregatedSiteData>(
    aggregatedSiteDataSerializer, aggregatedSiteDataDeserializer
)
