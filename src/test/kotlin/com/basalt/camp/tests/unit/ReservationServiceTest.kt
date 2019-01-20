package com.basalt.camp.tests.unit

import com.basalt.camp.api.reservation.ReservationCreationRequest
import com.basalt.camp.base.BaseTest
import com.basalt.camp.business.reservation.ReservationService
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate

class ReservationServiceTest : BaseTest() {

    val reservationService = ReservationService()

    @Test
    fun checkInCanNotBeEqualCheckOut() {
        val now = LocalDate.now()
        val creationRequest = ReservationCreationRequest(
            email = "test@test.com",
            name = "User Name",
            checkIn = now,
            checkOut = now
        )
        val createReservation = this.reservationService.createReservation(creationRequest)

        Assert.assertFalse(createReservation.success)
    }

    @Test
    fun checkInCanNotBeAfterCheckOut() {
        val creationRequest = ReservationCreationRequest(
            email = "test@test.com",
            name = "User Name",
            checkIn = LocalDate.now(),
            checkOut = LocalDate.now().minusDays(1)
        )
        val createReservation = this.reservationService.createReservation(creationRequest)

        Assert.assertFalse(createReservation.success)
    }

    @Test
    fun reservationPeriodCanNotBeGreaterThan3Days() {
        val creationRequest = ReservationCreationRequest(
            email = "test@test.com",
            name = "User Name",
            checkIn = LocalDate.now(),
            checkOut = LocalDate.now().plusDays(4)
        )
        val createReservation = this.reservationService.createReservation(creationRequest)

        Assert.assertFalse(createReservation.success)
    }

    @Test
    fun emailMustBeFilled() {
        val creationRequest = ReservationCreationRequest(
            email = "",
            name = "User Name",
            checkIn = LocalDate.now(),
            checkOut = LocalDate.now().plusDays(1)
        )
        val createReservation = this.reservationService.createReservation(creationRequest)

        Assert.assertFalse(createReservation.success)
    }

    @Test
    fun nameMustBeFilled() {
        val creationRequest = ReservationCreationRequest(
            email = "test@test.com",
            name = "   ",
            checkIn = LocalDate.now(),
            checkOut = LocalDate.now().plusDays(1)
        )
        val createReservation = this.reservationService.createReservation(creationRequest)

        Assert.assertFalse(createReservation.success)
    }
}