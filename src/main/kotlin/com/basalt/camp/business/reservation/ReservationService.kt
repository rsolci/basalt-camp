package com.basalt.camp.business.reservation

import com.basalt.camp.api.reservation.ReservationCreationPayload
import com.basalt.camp.api.reservation.ReservationCreationRequest
import com.basalt.camp.api.reservation.ReservationCreationResponse
import com.basalt.camp.business.user.User
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

@Service
class ReservationService {
    companion object {
        private val log = LoggerFactory.getLogger(ReservationService::class.java)
    }

    fun createReservation(reservationCreationRequest: ReservationCreationRequest): ReservationCreationResponse {
        log.info("Atempting to create reservation ({})", reservationCreationRequest)
        val checkIn: Instant = reservationCreationRequest.checkIn.atTime(12, 0).atOffset(ZoneOffset.UTC).toInstant()
        val checkOut: Instant = reservationCreationRequest.checkOut.atTime(12, 0).atOffset(ZoneOffset.UTC).toInstant()

        var reservationCreationResponse = validateOrder(checkIn, checkOut)
        reservationCreationResponse = validateReservationInterval(checkIn, checkOut).merge(reservationCreationResponse)

        if (reservationCreationRequest.name.isBlank()) {
            reservationCreationResponse = reservationCreationResponse.merge(ReservationCreationResponse(false, listOf("Name is required for reservation")))
        }
        if (reservationCreationRequest.email.isBlank()) {
            reservationCreationResponse = reservationCreationResponse.merge(ReservationCreationResponse(false, listOf("E-mail is required for reservation")))
        }

        if (!reservationCreationResponse.success) {
            return reservationCreationResponse
        }
        // TODO check existing user
        val user =
            User(id = UUID.randomUUID(),
                name = reservationCreationRequest.name,
                email = reservationCreationRequest.email)
        // TODO persist user

        // TODO check availability
        val reservation = Reservation(id = UUID.randomUUID(), checkIn = checkIn, checkOut = checkOut, owner = user)
        // TODO persist reservation

        return ReservationCreationResponse(success = true, payload = ReservationCreationPayload(reservation.id))
    }

    fun validateOrder(start: Instant, end: Instant): ReservationCreationResponse {
        if (start.isAfter(end) || start == end) {
            log.warn("Check-In is after or equals Check-Out")
            return ReservationCreationResponse(false, listOf("Check-out dat must be after check-in"))
        }
        return ReservationCreationResponse.emptySuccess()
    }

    fun validateReservationInterval(start: Instant, end: Instant): ReservationCreationResponse {
        if (Duration.between(start, end).toDays() > 3) {
            log.warn("Reservation period is greater than 3 days")
            return ReservationCreationResponse(false, listOf("Maximum reservation period is 3 days"))
        }
        return ReservationCreationResponse.emptySuccess()
    }
}