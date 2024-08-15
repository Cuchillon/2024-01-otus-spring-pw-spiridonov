package com.ferick.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties("plots")
data class PlotProperties @ConstructorBinding constructor(
    @NestedConfigurationProperty
    val visitors: Layout,
    @NestedConfigurationProperty
    val viewCounts: Layout,
    @NestedConfigurationProperty
    val viewPeriods: Layout
)

data class Layout @ConstructorBinding constructor(
    val title: String,
    val width: Int,
    val height: Int
)
