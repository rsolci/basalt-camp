package com.basalt.camp.reservation

import com.basalt.camp.api.reservation.ReservationCreationRequest
import com.basalt.camp.api.reservation.ReservationCreationResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/reservations"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
class ReservationRestController {

    @PostMapping
    fun create(reservationRequest: ReservationCreationRequest) : ReservationCreationResponse {
        TODO()
    }
}