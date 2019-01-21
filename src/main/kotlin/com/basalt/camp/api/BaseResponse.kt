package com.basalt.camp.api

open class BaseResponse(val success: Boolean, val messages: List<String> = emptyList(), val payload: BasePayload? = null) {
    fun merge(other: BaseResponse): BaseResponse {
        return BaseResponse(this.success && other.success, messages.union(other.messages).toList())
    }
}