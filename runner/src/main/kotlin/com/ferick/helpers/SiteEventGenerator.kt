package com.ferick.helpers

import com.ferick.common.model.SiteEventDto
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.random.Random

@Component
class SiteEventGenerator {

    fun generate(date: LocalDate): List<SiteEventDto> {
        return (0..23).flatMap { hour ->
            (0..59).flatMap { minute ->
                val (from, until) = workloadLevel(hour)
                secondsWithVisitors(from, until).map { second ->
                    SiteEventDto(
                        site = SITE,
                        dateTime = LocalDateTime.of(date, LocalTime.of(hour, minute, second)).toString(),
                        userId = UUID.randomUUID().toString(),
                        page = pages.random(),
                        period = Random.nextLong(1, 11)
                    )
                }
            }
        }
    }

    private fun workloadLevel(hour: Int): Pair<Int, Int> =
        when (hour) {
            in 0..8 -> 1 to 3
            in 9..19 -> 7 to 10
            in 20..23 -> 4 to 6
            else -> throw IllegalArgumentException("Wrong hour")
        }

    private fun secondsWithVisitors(from: Int, until: Int): List<Int> {
        val visitorNumber = Random.Default.nextInt(from, until)
        return (1..visitorNumber).map { Random.Default.nextInt(0, 60) }
    }

    companion object {
        private const val SITE = "site.com"
        private val pages = listOf("/index", "/index", "/index", "/blog", "/blog", "/questions")
    }
}
