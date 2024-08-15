package com.ferick

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VisualizerApplication

fun main(args: Array<String>) {
	runApplication<VisualizerApplication>(*args)
}
