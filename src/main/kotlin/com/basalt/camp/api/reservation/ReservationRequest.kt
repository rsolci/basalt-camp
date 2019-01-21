package com.basalt.camp.api.reservation

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

class ReservationRequest(
    val email: String,
    val name: String,
    @DateTimeFormat(pattern = "yyyy-MM-dd") val checkIn: LocalDate,
    @DateTimeFormat(pattern = "yyyy-MM-dd") val checkOut: LocalDate
) {
    override fun toString(): String {
        return "ReservationRequest(email='$email', name='$name', checkIn=$checkIn, checkOut=$checkOut)"
    }
}