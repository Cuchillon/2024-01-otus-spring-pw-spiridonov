package com.ferick

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ferick.generator.AggregationGenerator
import com.ferick.model.entities.AggregatedSiteDataEntity
import com.ferick.repositories.AggregatedSiteDataRepository
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit


@SpringBootTest
class VisualizerApplicationTests {

	@Autowired
	private lateinit var aggregatedSiteDataRepository: AggregatedSiteDataRepository

	@BeforeEach
	fun clearDatabase() {
		aggregatedSiteDataRepository.deleteAll()
	}

	@Test
	fun contextLoads() {
		val aggregatedSiteData = AggregationGenerator.generate()
		aggregatedSiteDataRepository.saveAll(
			aggregatedSiteData.map {
				AggregatedSiteDataEntity(
					startTime = LocalDateTime.parse(it.startTime),
					endTime = LocalDateTime.parse(it.endTime),
					visitorNumber = it.visitorNumber,
					pageStats = it.pageStats
				)
			}
		)
	}

	@Test
	fun loadDataToTopic() {
		val bootstrapServers = "localhost:9092"
		val topic = "output-topic"
		val aggregatedSiteData = AggregationGenerator.generate().map {
			"site.com" to jacksonObjectMapper().writeValueAsString(it)
		}
		createTopics(bootstrapServers, listOf(topic))
		setRecords(bootstrapServers, topic, aggregatedSiteData)
	}

	fun createTopics(bootstrapServers: String, topicNames: List<String>) {
		try {
			getAdminClient(bootstrapServers).use { client ->
				val existedTopics = client.listTopics().names().get()
				val topics = topicNames
					.filter { !existedTopics.contains(it) }
					.map { NewTopic(it, 1, 1) }
				client.createTopics(topics).all()[10, TimeUnit.SECONDS]
			}
		} catch (e: Exception) {
			throw RuntimeException("Failed to create topics", e)
		}
	}

	fun setRecords(bootstrapServers: String, topicName: String, pairs: List<Pair<String, String>>) {
		val producer: KafkaProducer<String, String> = getProducer(bootstrapServers)
		pairs.forEach { pair ->
			try {
				producer.send(ProducerRecord(topicName, pair.first, pair.second)).get()
			} catch (e: java.lang.Exception) {
				throw java.lang.RuntimeException("Failed to set record", e)
			}
		}
	}

	private fun getAdminClient(bootstrapServers: String): AdminClient {
		val properties = Properties()
		properties[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
		return AdminClient.create(properties)
	}

	private fun getProducer(bootstrapServers: String): KafkaProducer<String, String> {
		val properties = Properties()
		properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
		properties[ConsumerConfig.CLIENT_ID_CONFIG] = UUID.randomUUID().toString()
		return KafkaProducer(properties, StringSerializer(), StringSerializer())
	}
}
