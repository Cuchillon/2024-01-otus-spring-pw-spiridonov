package com.ferick.converters

import com.fasterxml.jackson.databind.ObjectMapper
import com.ferick.common.model.SiteEventAggregator
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serdes.WrapperSerde
import org.apache.kafka.common.serialization.Serializer
import org.springframework.stereotype.Component

@Component
class SiteEventAggregatorSerializer(
    private val objectMapper: ObjectMapper
) : Serializer<SiteEventAggregator> {

    override fun serialize(topic: String, data: SiteEventAggregator): ByteArray =
        objectMapper.writeValueAsBytes(data)
}

@Component
class SiteEventAggregatorDeserializer(
    private val objectMapper: ObjectMapper
) : Deserializer<SiteEventAggregator> {

    override fun deserialize(topic: String, data: ByteArray): SiteEventAggregator =
        objectMapper.readValue(data, SiteEventAggregator::class.java)
}

@Component
class SiteEventAggregatorSerde(
    siteEventAggregatorSerializer: SiteEventAggregatorSerializer,
    siteEventAggregatorDeserializer: SiteEventAggregatorDeserializer
) : WrapperSerde<SiteEventAggregator>(
    siteEventAggregatorSerializer, siteEventAggregatorDeserializer
)
