package com.ferick.service

import com.ferick.model.dto.PeriodRequest
import com.ferick.model.dto.PlotType
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.*

interface VisualizingService {

    fun getAvailablePeriod(): Pair<LocalDateTime, LocalDateTime>

    fun visualizeDataBetween(request: PeriodRequest): EnumMap<PlotType, List<Path>>
}
