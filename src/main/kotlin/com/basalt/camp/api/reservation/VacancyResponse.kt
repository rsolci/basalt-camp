package com.basalt.camp.api.reservation

import com.basalt.camp.api.BaseResponse

class VacancyResponse(success: Boolean, messages: List<String> = emptyList(), payload: VacancyPayload? = null) : BaseResponse(success, messages, payload)