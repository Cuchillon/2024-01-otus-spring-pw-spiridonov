package com.ferick.configuration

import com.ferick.common.model.AggregatedSiteData
import com.ferick.configuration.properties.SourceProperties
import com.ferick.converters.AggregatedSiteDataDeserializer
import com.ferick.service.AggregationService
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.Pollers
import org.springframework.integration.dsl.integrationFlow
import org.springframework.integration.kafka.dsl.Kafka
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConsumerProperties


@Configuration
@EnableKafka
@EnableIntegration
@IntegrationComponentScan(basePackages = ["com.ferick.service"])
@EnableConfigurationProperties(SourceProperties::class)
class KafkaIntegrationConfiguration(
    private val kafkaProperties: KafkaProperties,
    private val sourceProperties: SourceProperties,
    private val aggregationService: AggregationService
) {

    @Bean
    fun sourceFlow(cf: ConsumerFactory<String, AggregatedSiteData>): IntegrationFlow =
        integrationFlow(
            Kafka.inboundChannelAdapter(cf, ConsumerProperties(sourceProperties.topicName)),
            { poller(Pollers.fixedDelay(sourceProperties.pollingPeriod)) }
        ) {
            handle(aggregationService, AGGREGATION_SERVICE_METHOD_NAME)
        }

    @Bean
    fun consumerFactory(
        aggregatedSiteDataDeserializer: AggregatedSiteDataDeserializer
    ): ConsumerFactory<String, AggregatedSiteData> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        props[ConsumerConfig.GROUP_ID_CONFIG] = kafkaProperties.consumer.groupId
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        return DefaultKafkaConsumerFactory<String, AggregatedSiteData>(props).also {
            it.setValueDeserializer(aggregatedSiteDataDeserializer)
        }
    }

    companion object {
        private const val AGGREGATION_SERVICE_METHOD_NAME = "saveAggregationSiteData"
    }
}
