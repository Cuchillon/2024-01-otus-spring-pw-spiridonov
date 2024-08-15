package com.ferick.model.dto

import java.time.LocalDateTime
import java.util.*

data class PeriodData(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val imageNames: EnumMap<PlotType, List<String>>
)
