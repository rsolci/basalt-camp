package com.basalt.camp.business.reservation

import com.basalt.camp.api.reservation.ReservationCreationPayload
import com.basalt.camp.api.reservation.ReservationRequest
import com.basalt.camp.api.reservation.ReservationResponse
import com.basalt.camp.business.user.User
import com.basalt.camp.business.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID

@Service
class ReservationService(
        val userService: UserService,
        val reservationRepository: ReservationRepository
) {
    companion object {
        private val log = LoggerFactory.getLogger(ReservationService::class.java)
    }

    fun createReservation(reservationRequest: ReservationRequest): ReservationResponse {
        log.info("Attempting to create reservation ({})", reservationRequest)
        val checkIn: Instant = normalizeDateToMidDay(reservationRequest.checkIn)
        val checkOut: Instant = normalizeDateToMidDay(reservationRequest.checkOut)

        var reservationCreationResponse = validateBasicFields(checkIn, checkOut, reservationRequest)

        if (!reservationCreationResponse.success) {
            return reservationCreationResponse
        }
        log.info("Basic field are valid")

        // TODO cache user?
        val user =
                userService.findByEmail(reservationRequest.email) ?: userService.save(User(id = UUID.randomUUID(),
                        name = reservationRequest.name,
                        email = reservationRequest.email))

        val newReservationId = UUID.randomUUID()
        val otherReservations = reservationRepository.findOtherReservationsWithinPeriod(newReservationId, checkIn, checkOut)
        if (!otherReservations.isEmpty()) {
            log.warn("Found overlapping reservations for this request")
            return ReservationResponse(success = false, messages = listOf("This date range in unavailable"))
        }

        val reservation = Reservation(id = newReservationId, checkIn = checkIn, checkOut = checkOut, owner = user)

        reservationRepository.save(reservation)

        return ReservationResponse(success = true, payload = ReservationCreationPayload(reservation.id))
    }

    fun updateReservation(reservationId: UUID, reservationRequest: ReservationRequest): ReservationResponse {
        log.info("Attempting to edit reservation {} to {}", reservationId, reservationRequest)

        val checkIn: Instant = normalizeDateToMidDay(reservationRequest.checkIn)
        val checkOut: Instant = normalizeDateToMidDay(reservationRequest.checkOut)

        var reservationCreationResponse = validateBasicFields(checkIn, checkOut, reservationRequest)
        if (!reservationCreationResponse.success) {
            return reservationCreationResponse
        }
        // TODO check availability

        val reservation = reservationRepository.findById(reservationId)
        if (reservation.isEmpty) {
            return ReservationResponse(false, listOf("Cant find reservation for identifier $reservationId"))
        }

        val user =
                userService.findByEmail(reservationRequest.email) ?: userService.save(User(id = UUID.randomUUID(),
                        name = reservationRequest.name,
                        email = reservationRequest.email))

        val newReservation = Reservation(
                id = reservationId,
                checkIn = checkIn,
                checkOut = checkOut,
                owner = user,
                createdAt = reservation.get().createdAt
        )
        reservationRepository.save(newReservation)
        return ReservationResponse(true, emptyList())
    }

    fun cancelReservation(reservationId: UUID): ReservationResponse {
        val reservationOp = reservationRepository.findById(reservationId)
        if (reservationOp.isEmpty) {
            return ReservationResponse(false, listOf("Cant find reservation for identifier $reservationId"))
        }

        val reservation = reservationOp.get()
        val cancelledReservation = Reservation(
                id = reservation.id,
                status = ReservationStatus.CANCELLED,
                owner = reservation.owner,
                checkIn = reservation.checkIn,
                checkOut = reservation.checkOut,
                createdAt = reservation.createdAt
        )
        reservationRepository.save(cancelledReservation)
        return ReservationResponse(true, emptyList())
    }

    private fun validateBasicFields(checkIn: Instant, checkOut: Instant, reservationRequest: ReservationRequest): ReservationResponse {
        var reservationCreationResponse = validateOrder(checkIn, checkOut)
        reservationCreationResponse = validateReservationInterval(checkIn, checkOut).merge(reservationCreationResponse)
        if (checkIn.isBefore(Instant.now())) {
            reservationCreationResponse =
                    reservationCreationResponse.merge(ReservationResponse(false,
                            listOf("Cant make reservations for past dates")))
        }

        if (Duration.between(normalizeDateToMidDay(LocalDate.now()), checkIn).toDays() < 1) {
            reservationCreationResponse =
                    reservationCreationResponse.merge(ReservationResponse(false,
                            listOf("Reservation need to be made at least one day before")))
        }

        if (Duration.between(normalizeDateToMidDay(LocalDate.now()), checkIn).toDays() > 30) {
            reservationCreationResponse =
                    reservationCreationResponse.merge(ReservationResponse(false,
                            listOf("Reservation can only be made up to 1 month before")))
        }

        if (reservationRequest.name.isBlank()) {
            reservationCreationResponse =
                    reservationCreationResponse.merge(ReservationResponse(false,
                            listOf("Name is required for reservation")))
        }
        if (reservationRequest.email.isBlank()) {
            reservationCreationResponse =
                    reservationCreationResponse.merge(ReservationResponse(false,
                            listOf("E-mail is required for reservation")))
        }
        return reservationCreationResponse
    }

    private fun validateOrder(start: Instant, end: Instant): ReservationResponse {
        if (start.isAfter(end) || start == end) {
            log.warn("Check-In is after or equals Check-Out")
            return ReservationResponse(false, listOf("Check-out date must be after check-in"))
        }
        return ReservationResponse.emptySuccess()
    }

    private fun validateReservationInterval(start: Instant, end: Instant): ReservationResponse {
        if (Duration.between(start, end).toDays() > 3) {
            log.warn("Reservation period is greater than 3 days")
            return ReservationResponse(false, listOf("Maximum reservation period is 3 days"))
        }
        return ReservationResponse.emptySuccess()
    }

    private fun normalizeDateToMidDay(localDate: LocalDate) =
            localDate.atTime(12, 0).atOffset(ZoneOffset.UTC).toInstant()
}