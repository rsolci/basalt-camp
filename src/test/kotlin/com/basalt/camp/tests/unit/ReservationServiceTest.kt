package com.basalt.camp.tests.unit

import com.basalt.camp.api.reservation.ReservationRequest
import com.basalt.camp.base.BaseTest
import com.basalt.camp.mocks.reservation.ReservationServiceMock
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate

class ReservationServiceTest : BaseTest() {

    private val reservationService = ReservationServiceMock()

    @Test
    fun checkInCanNotBeEqualCheckOut() {
        val now = LocalDate.now()
        val creationRequest = ReservationRequest(
            email = "test@test.com",
            name = "User Name",
            checkIn = now,
            checkOut = now
        )
        val createReservation = this.reservationService.createReservation(creationRequest)

        Assert.assertFalse(createReservation.success)
        Assert.assertTrue(createReservation.messages.contains("Check-out date must be after check-in"))
    }

    @Test
    fun checkInCanNotBeAfterCheckOut() {
        val creationRequest = ReservationRequest(
            email = "test@test.com",
            name = "User Name",
            checkIn = LocalDate.now(),
            checkOut = LocalDate.now().minusDays(1)
        )
        val createReservation = this.reservationService.createReservation(creationRequest)

        Assert.assertFalse(createReservation.success)
        Assert.assertTrue(createReservation.messages.contains("Check-out date must be after check-in"))
    }

    @Test
    fun reservationPeriodCanNotBeGreaterThan3Days() {
        val creationRequest = ReservationRequest(
            email = "test@test.com",
            name = "User Name",
            checkIn = LocalDate.now(),
            checkOut = LocalDate.now().plusDays(4)
        )
        val createReservation = this.reservationService.createReservation(creationRequest)

        Assert.assertFalse(createReservation.success)
        Assert.assertTrue(createReservation.messages.contains("Maximum reservation period is 3 days"))
    }

    @Test
    fun emailMustBeFilled() {
        val creationRequest = ReservationRequest(
            email = "",
            name = "User Name",
            checkIn = LocalDate.now(),
            checkOut = LocalDate.now().plusDays(1)
        )
        val createReservation = this.reservationService.createReservation(creationRequest)

        Assert.assertFalse(createReservation.success)
        Assert.assertTrue(createReservation.messages.contains("E-mail is required for reservation"))
    }

    @Test
    fun nameMustBeFilled() {
        val creationRequest = ReservationRequest(
            email = "test@test.com",
            name = "   ",
            checkIn = LocalDate.now(),
            checkOut = LocalDate.now().plusDays(1)
        )
        val createReservation = this.reservationService.createReservation(creationRequest)

        Assert.assertFalse(createReservation.success)
        Assert.assertTrue(createReservation.messages.contains("Name is required for reservation"))
    }

    @Test
    fun reservationMustBeMadeAtLeastOneDayBefore() {
        val creationRequest = ReservationRequest(
            email = "test@test.com",
            name = "User Name",
            checkIn = LocalDate.now(),
            checkOut = LocalDate.now().plusDays(2)
        )
        val createReservation = this.reservationService.createReservation(creationRequest)

        Assert.assertFalse(createReservation.success)
        Assert.assertTrue(createReservation.messages.contains("Reservation need to be made at least one day before"))
    }

    @Test
    fun cantCreateReservationForPastDates() {
        val creationRequest = ReservationRequest(
            email = "test@test.com",
            name = "User Name",
            checkIn = LocalDate.now().minusDays(5),
            checkOut = LocalDate.now().minusDays(3)
        )
        val createReservation = this.reservationService.createReservation(creationRequest)

        Assert.assertFalse(createReservation.success)
        Assert.assertTrue(createReservation.messages.contains("Reservation need to be made at least one day before"))
    }

    @Test
    fun reservationCantBeMadeMoreThanOneMonthBefore() {
        val creationRequest = ReservationRequest(
            email = "test@test.com",
            name = "User Name",
            checkIn = LocalDate.now().plusDays(45),
            checkOut = LocalDate.now().plusDays(47)
        )
        val createReservation = this.reservationService.createReservation(creationRequest)

        Assert.assertFalse(createReservation.success)
        Assert.assertTrue(createReservation.messages.contains("Reservation can only be made up to 1 month before"))
    }
}