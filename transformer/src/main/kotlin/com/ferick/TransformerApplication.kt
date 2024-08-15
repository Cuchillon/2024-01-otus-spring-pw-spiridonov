package com.ferick

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TransformerApplication

fun main(args: Array<String>) {
	runApplication<TransformerApplication>(*args)
}
