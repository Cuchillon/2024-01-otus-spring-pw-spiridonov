package com.ferick.generator

import com.ferick.common.model.AggregatedSiteData
import com.ferick.common.model.PageStat
import java.time.LocalDateTime
import kotlin.random.Random

object AggregationGenerator {

    private val pages = listOf("/index", "/blog", "/questions")

    fun generate(minutes: Int = 1440): List<AggregatedSiteData> {
        val now = LocalDateTime.now()
        return (minutes downTo 0).map {
            val startTime = now.minusMinutes(it.toLong())
            val endTime = startTime.plusMinutes(1)
            val statsPerMinute = statsPerMinute()
            AggregatedSiteData(
                startTime = startTime.toString(),
                endTime = endTime.toString(),
                visitorNumber = statsPerMinute.first,
                pageStats = statsPerMinute.second
            )
        }
    }

    private fun statsPerMinute(): Pair<Long, Map<String, PageStat>> {
        val visitorNumber = Random.Default.nextLong(0, 100)
        return if (visitorNumber == 0L) {
            0L to emptyMap()
        } else {
            val blogCount = visitorNumber / 3 * 2
            val questionsCount = visitorNumber / 3
            val pageStats = mutableMapOf("/index" to PageStat(visitorNumber.toInt(), countToPeriod(visitorNumber)))
            if (blogCount > 0L) {
                pageStats["/blog"] = PageStat(blogCount.toInt(), countToPeriod(blogCount))
            }
            if (questionsCount > 0L) {
                pageStats["/questions"] = PageStat(questionsCount.toInt(), countToPeriod(questionsCount))
            }
            visitorNumber to pageStats
        }
    }

    private fun countToPeriod(count: Long) = (0 until count).sumOf {
        Random.nextLong(1, 11)
    }
}
