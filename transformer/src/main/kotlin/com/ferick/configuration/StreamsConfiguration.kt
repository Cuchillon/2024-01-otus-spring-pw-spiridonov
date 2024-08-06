package com.ferick.configuration

import com.ferick.common.model.AggregatedSiteData
import com.ferick.common.model.PageStat
import com.ferick.common.model.SiteEventAggregator
import com.ferick.configuration.properties.TopicProperties
import com.ferick.converters.AggregatedSiteDataSerde
import com.ferick.converters.SiteEventAggregatorSerde
import com.ferick.converters.SiteEventSerde
import com.ferick.converters.SiteEventTimeExtractor
import com.ferick.extensions.endTime
import com.ferick.extensions.startTime
import com.ferick.service.initializers.AdminInitializer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG
import org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG
import org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.kstream.Suppressed
import org.apache.kafka.streams.kstream.TimeWindows
import org.apache.kafka.streams.state.WindowStore
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration
import java.time.Duration


@Configuration
@EnableKafka
@EnableKafkaStreams
@EnableConfigurationProperties(TopicProperties::class)
class StreamsConfiguration(
    private val kafkaProperties: KafkaProperties,
    private val topicProperties: TopicProperties,
    private val siteEventSerde: SiteEventSerde,
    private val siteEventAggregatorSerde: SiteEventAggregatorSerde,
    private val aggregatedSiteDataSerde: AggregatedSiteDataSerde,
    private val siteEventTimeExtractor: SiteEventTimeExtractor
) {

    @Bean(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    fun kStreamsConfig(): KafkaStreamsConfiguration {
        val props: MutableMap<String, Any> = HashMap()
        props[APPLICATION_ID_CONFIG] = kafkaProperties.streams.applicationId
        props[BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        props[DEFAULT_KEY_SERDE_CLASS_CONFIG] = StringSerde::class.java.name
        props[DEFAULT_VALUE_SERDE_CLASS_CONFIG] = StringSerde::class.java.name

        return KafkaStreamsConfiguration(props)
    }

    @Bean
    fun adminInitializer(): AdminInitializer = AdminInitializer(
        kafkaProperties,
        topicProperties
    )

    @Bean
    @DependsOn("adminInitializer")
    fun kStream(streamsBuilder: StreamsBuilder): KStream<String, AggregatedSiteData> =
        streamsBuilder.stream(
            topicProperties.sourceTopicName,
            Consumed.with(Serdes.String(), siteEventSerde, siteEventTimeExtractor, Topology.AutoOffsetReset.LATEST)
        )
            .groupByKey()
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1L)))
            .aggregate(
                {
                    SiteEventAggregator()
                },
                { _, siteEvent, aggregator ->
                    val users = aggregator.users
                    users.add(siteEvent.userId)
                    val pageStats = aggregator.pageStats
                    pageStats.merge(siteEvent.page, PageStat(1, siteEvent.period)) { v1, v2 ->
                        PageStat(v1.viewCount + v2.viewCount, v1.viewPeriod + v2.viewPeriod)
                    }
                    SiteEventAggregator(users, pageStats)
                },
                Materialized
                    .`as`<String, SiteEventAggregator, WindowStore<Bytes, ByteArray>>("aggregation-store")
                    .withValueSerde(siteEventAggregatorSerde)
            )
            .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
            .toStream().map { key, aggregator ->
                KeyValue.pair(
                    key.key(),
                    AggregatedSiteData(
                        startTime = key.startTime(),
                        endTime = key.endTime(),
                        visitorNumber = aggregator.users.size.toLong(),
                        pageStats = aggregator.pageStats
                    )
                )
            }
            .apply {
                to(topicProperties.targetTopicName, Produced.valueSerde(aggregatedSiteDataSerde))
            }
}
