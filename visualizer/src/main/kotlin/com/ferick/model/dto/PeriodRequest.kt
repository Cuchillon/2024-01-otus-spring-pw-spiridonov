package com.ferick.model.dto

import jakarta.validation.constraints.NotBlank

class PeriodRequest {
    @NotBlank(message = "Start time must not be blank")
    var startTime: String? = null
    @NotBlank(message = "End time must not be blank")
    var endTime: String? = null
}
