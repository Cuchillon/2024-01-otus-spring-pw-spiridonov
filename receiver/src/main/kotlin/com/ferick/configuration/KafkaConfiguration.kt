package com.ferick.configuration

import com.ferick.common.model.SiteEvent
import com.ferick.configuration.properties.TargetProperties
import com.ferick.converters.SiteEventSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

@Configuration
@EnableKafka
@EnableConfigurationProperties(TargetProperties::class)
class KafkaConfiguration(
    private val kafkaProperties: KafkaProperties
) {

    @Bean
    fun kafkaSender(senderOptions: SenderOptions<String, SiteEvent>): KafkaSender<String, SiteEvent> =
        KafkaSender.create(senderOptions)

    @Bean
    fun senderOptions(siteEventSerializer: SiteEventSerializer): SenderOptions<String, SiteEvent> {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        props[ProducerConfig.CLIENT_ID_CONFIG] = kafkaProperties.producer.clientId
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        return SenderOptions.create<String, SiteEvent>(props)
            .withValueSerializer(siteEventSerializer)
    }
}
