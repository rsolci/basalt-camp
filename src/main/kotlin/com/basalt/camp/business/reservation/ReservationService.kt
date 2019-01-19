package com.basalt.camp.business.reservation

import com.basalt.camp.api.reservation.ReservationCreationPayload
import com.basalt.camp.api.reservation.ReservationCreationRequest
import com.basalt.camp.api.reservation.ReservationCreationResponse
import com.basalt.camp.business.user.User
import com.basalt.camp.helper.DateFormatter
import org.springframework.stereotype.Service
import java.text.ParseException
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

@Service
class ReservationService {

    fun createReservation(reservationCreationRequest: ReservationCreationRequest) : ReservationCreationResponse{
        val checkIn: Instant = reservationCreationRequest.checkIn.atTime(12, 0).atOffset(ZoneOffset.UTC).toInstant()
        val checkOut: Instant = reservationCreationRequest.checkOut.atTime(12, 0).atOffset(ZoneOffset.UTC).toInstant()
        // TODO validate valid interval, in < out
        if (reservationCreationRequest.name.isEmpty()) {
            return ReservationCreationResponse(false, listOf("Name is required for reservation"))
        }
        if (reservationCreationRequest.email.isEmpty()) {
            return ReservationCreationResponse(false, listOf("E-mail is required for reservation"))
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
}