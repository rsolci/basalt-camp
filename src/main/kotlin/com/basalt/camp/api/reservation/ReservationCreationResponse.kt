package com.basalt.camp.api.reservation

import com.basalt.camp.api.BaseResponse

class ReservationCreationResponse(success: Boolean,
                                  messages: List<String> = emptyList(),
                                  payload: ReservationCreationPayload? = null) : BaseResponse(success,
    messages,
    payload) {

    companion object {
        fun emptySuccess() = ReservationCreationResponse(true, emptyList())
    }

    fun merge(response: ReservationCreationResponse): ReservationCreationResponse {
        return ReservationCreationResponse(this.success && response.success, messages.union(response.messages).toList())
    }
}