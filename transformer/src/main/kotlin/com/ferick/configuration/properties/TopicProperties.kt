package com.ferick.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties("topics")
data class TopicProperties @ConstructorBinding constructor(
    val sourceTopicName: String,
    val targetTopicName: String
)
