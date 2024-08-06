package com.ferick

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ferick.common.model.SiteEvent
import com.ferick.configuration.StreamsConfiguration
import com.ferick.configuration.properties.TopicProperties
import com.ferick.converters.AggregatedSiteDataDeserializer
import com.ferick.converters.AggregatedSiteDataSerde
import com.ferick.converters.AggregatedSiteDataSerializer
import com.ferick.converters.SiteEventAggregatorDeserializer
import com.ferick.converters.SiteEventAggregatorSerde
import com.ferick.converters.SiteEventAggregatorSerializer
import com.ferick.converters.SiteEventDeserializer
import com.ferick.converters.SiteEventSerde
import com.ferick.converters.SiteEventSerializer
import com.ferick.converters.SiteEventTimeExtractor
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TopologyTestDriver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

class SiteEventTest {

	private val objectMapper = jacksonObjectMapper()
	private val siteEventSerializer = SiteEventSerializer(objectMapper)
	private val siteEventDeserializer = SiteEventDeserializer(objectMapper)
	private val siteEventSerde: SiteEventSerde = SiteEventSerde(siteEventSerializer, siteEventDeserializer)
	private val siteEventAggregatorSerde = SiteEventAggregatorSerde(
		SiteEventAggregatorSerializer(objectMapper), SiteEventAggregatorDeserializer(objectMapper)
	)
	private val aggregatedSiteDataSerializer = AggregatedSiteDataSerializer(objectMapper)
	private val aggregatedSiteDataDeserializer = AggregatedSiteDataDeserializer(objectMapper)
	private val aggregatedSiteDataSerde = AggregatedSiteDataSerde(
		aggregatedSiteDataSerializer, aggregatedSiteDataDeserializer
	)
	private val siteEventTimeExtractor = SiteEventTimeExtractor()
	private val streamsConfiguration = StreamsConfiguration(
		kafkaProperties = KafkaProperties(),
		topicProperties = TopicProperties(INPUT_TOPIC, OUTPUT_TOPIC),
		aggregatedSiteDataSerde = aggregatedSiteDataSerde,
		siteEventAggregatorSerde = siteEventAggregatorSerde,
		siteEventSerde = siteEventSerde,
		siteEventTimeExtractor = siteEventTimeExtractor
	)

	@Test
	fun checkStreamBuilder() {
		val driver = getPageTopologyTestDriver()
		val inputTopic = driver.createInputTopic(INPUT_TOPIC, StringSerializer(), siteEventSerializer)
		val outputTopic = driver.createOutputTopic(OUTPUT_TOPIC, StringDeserializer(), aggregatedSiteDataDeserializer)

		inputTopic.pipeKeyValueList(events)

		outputTopic.readKeyValuesToList().forEach {
			assertThat(it.key == SITE)
			assertThat(it.value.visitorNumber).isGreaterThan(0)
		}
	}

	private fun getPageTopologyTestDriver(): TopologyTestDriver {
		val properties = Properties()
		properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String()::class.java.name)
		properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String()::class.java.name)
		val topology = StreamsBuilder().apply {
			streamsConfiguration.kStream(this)
		}.build()
		return TopologyTestDriver(topology, properties)
	}

	companion object {
		private const val INPUT_TOPIC = "input-topic"
		private const val OUTPUT_TOPIC = "output-topic"
		private const val SITE = "site.com"
		private val users = listOf(
			UUID.randomUUID().toString(),
			UUID.randomUUID().toString(),
			UUID.randomUUID().toString()
		)
		private val paths = listOf("/index", "/blog", "/questions")
		private val now = LocalDateTime.now()

		private val events = (300 downTo 5).step(5).map {
			KeyValue.pair(
				SITE,
				SiteEvent(
					dateTime = now.minusSeconds(it.toLong()).toString(),
					userId = users.random(),
					page = paths.random(),
					period = Random.Default.nextLong(1, 11)
				)
			)
		}
	}
}
