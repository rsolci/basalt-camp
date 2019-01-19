package com.basalt.camp.api

abstract class BaseResponse(val success: Boolean, val messages: List<String> = emptyList(), val payload: BasePayload? = null)