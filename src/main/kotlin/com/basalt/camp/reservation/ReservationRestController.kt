package com.basalt.camp.reservation

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/reservations"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
class ReservationRestController {

}