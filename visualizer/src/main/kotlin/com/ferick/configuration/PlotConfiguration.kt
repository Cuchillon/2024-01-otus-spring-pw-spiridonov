package com.ferick.configuration

import com.ferick.configuration.properties.PlotProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(PlotProperties::class)
class PlotConfiguration
