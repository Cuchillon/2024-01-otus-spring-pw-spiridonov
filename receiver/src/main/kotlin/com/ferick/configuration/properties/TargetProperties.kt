package com.ferick.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties("target")
data class TargetProperties @ConstructorBinding constructor(
    val topicName: String
)
