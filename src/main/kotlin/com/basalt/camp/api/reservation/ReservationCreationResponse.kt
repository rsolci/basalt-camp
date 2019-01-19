package com.basalt.camp.api.reservation

import com.basalt.camp.api.BaseResponse

class ReservationCreationResponse(success: Boolean,
                                  messages: List<String> = emptyList(),
                                  payload: ReservationCreationPayload? = null) : BaseResponse(success,
    messages,
    payload)