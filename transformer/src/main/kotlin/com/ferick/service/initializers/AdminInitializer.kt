package com.ferick.service.initializers

import com.ferick.configuration.properties.TopicProperties
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import java.util.*
import java.util.concurrent.TimeUnit

class AdminInitializer(
    private val kafkaProperties: KafkaProperties,
    private val topicProperties: TopicProperties
) : InitializingBean {

    override fun afterPropertiesSet() {
        createTopics(
            kafkaProperties.bootstrapServers[0],
            listOf(topicProperties.sourceTopicName, topicProperties.targetTopicName)
        )
    }

    private fun createTopics(bootstrapServers: String, topicNames: List<String>) {
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

    private fun getAdminClient(bootstrapServers: String): AdminClient {
        val properties = Properties()
        properties[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        return AdminClient.create(properties)
    }
}
