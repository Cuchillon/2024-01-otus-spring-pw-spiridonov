package com.ferick.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties("source")
data class SourceProperties @ConstructorBinding constructor(
    val topicName: String,
    val pollingPeriod: Long
)
