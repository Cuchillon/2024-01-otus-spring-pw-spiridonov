package com.ferick.service

import com.ferick.model.dto.PeriodData
import com.ferick.model.dto.PeriodRequest
import java.time.LocalDateTime

interface VisualizingService {

    fun getAvailablePeriod(): Pair<LocalDateTime, LocalDateTime>

    fun visualizeDataBetween(request: PeriodRequest): PeriodData
}
