package com.ferick.common.model

data class SiteEvent(
    val dateTime: String,
    val userId: String,
    val page: String,
    val period: Long
)
