package com.basalt.camp.api.reservation

import com.basalt.camp.api.BaseResponse

class ReservationResponse(success: Boolean,
                          messages: List<String> = emptyList(),
                          payload: ReservationCreationPayload? = null) : BaseResponse(success,
    messages,
    payload) {

    companion object {
        fun emptySuccess() = ReservationResponse(true, emptyList())
    }

    fun merge(response: ReservationResponse): ReservationResponse {
        return ReservationResponse(this.success && response.success, messages.union(response.messages).toList())
    }
}