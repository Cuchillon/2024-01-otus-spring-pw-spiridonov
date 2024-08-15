package com.ferick.extensions

import org.apache.kafka.streams.kstream.Windowed
import java.time.LocalDateTime
import java.time.ZoneId

fun Windowed<*>.startTime(): String =
    LocalDateTime.ofInstant(this.window().startTime(), ZoneId.systemDefault()).toString()

fun Windowed<*>.endTime(): String =
    LocalDateTime.ofInstant(this.window().endTime(), ZoneId.systemDefault()).toString()