package com.ferick.common.model

data class AggregatedSiteData(
    val startTime: String,
    val endTime: String,
    val visitorNumber: Long,
    val pageStats: Map<String, PageStat>
)

data class SiteEventAggregator(
    val users: MutableSet<String> = mutableSetOf(),
    val pageStats: MutableMap<String, PageStat> = mutableMapOf()
)

data class PageStat(
    val viewCount: Int,
    val viewPeriod: Long
)
