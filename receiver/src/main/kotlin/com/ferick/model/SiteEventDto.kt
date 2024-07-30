package com.ferick.model

data class SiteEventDto(
    val site: String,
    val dateTime: String,
    val userId: String,
    val page: String,
    val period: Long
)
