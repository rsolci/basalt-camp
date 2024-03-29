package com.basalt.camp.business.reservation

import com.basalt.camp.api.reservation.ReservationRequest
import com.basalt.camp.api.reservation.ReservationResponse
import com.basalt.camp.api.reservation.VacancyResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping(path = [ReservationRestController.PATH], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
class ReservationRestController(
        val reservationService: ReservationService
) {
    companion object {
        const val PATH = "/reservations"
        const val ID = "/{id}"
        const val VACANCY = "/vacancy"
    }

    @PostMapping
    fun create(@RequestBody reservationRequest: ReservationRequest): ReservationResponse {
        return reservationService.createReservation(reservationRequest)
    }

    @PutMapping(path = [ReservationRestController.ID])
    fun modify(@RequestBody reservationRequest: ReservationRequest, @PathVariable("id") reservationId: UUID): ReservationResponse {
        return reservationService.updateReservation(reservationId, reservationRequest)
    }

    @DeleteMapping(path = [ReservationRestController.ID])
    fun cancel(@PathVariable("id") reservationId: UUID): ReservationResponse {
        return reservationService.cancelReservation(reservationId)
    }

    @GetMapping(path = [ReservationRestController.VACANCY])
    fun vacancy(@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam start: LocalDate?,
                @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam end: LocalDate?): VacancyResponse {
        return reservationService.vacancy(start ?: LocalDate.now(), end ?: LocalDate.now().plusDays(30))
    }
}