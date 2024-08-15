package com.ferick.converters

import com.ferick.common.model.SiteEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.streams.processor.TimestampExtractor
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class SiteEventTimeExtractor : TimestampExtractor {

    override fun extract(record: ConsumerRecord<Any, Any>, partitionTime: Long): Long {
        val dateTime = LocalDateTime.parse((record.value() as SiteEvent).dateTime)
        return dateTime.toInstant(ZoneOffset.ofHours(3)).toEpochMilli()
    }
}
