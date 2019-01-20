package com.basalt.camp.tests.integration

import com.basalt.camp.api.reservation.ReservationCreationPayload
import com.basalt.camp.api.reservation.ReservationCreationResponse
import com.basalt.camp.base.BaseIntegrationTest
import com.basalt.camp.business.reservation.ReservationRepository
import com.basalt.camp.mocks.reservation.ReservationJsonRequest
import com.mashape.unirest.http.Unirest
import org.apache.http.HttpStatus
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

class ReservationTest: BaseIntegrationTest() {

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @Test
    fun shouldCreateReservation() {
        val creationRequest = ReservationJsonRequest(
            email = "test@test.com",
            name = "Good name",
            checkIn = LocalDate.now().plusDays(1).toString(),
            checkOut = LocalDate.now().plusDays(3).toString()
        )

        val httpResponse =
            Unirest.post(apiUrl("/reservations")).body(creationRequest)
                .asObject(ReservationCreationResponse::class.java)
        Assert.assertEquals(HttpStatus.SC_OK, httpResponse.status)
        val reservationCreationResponse = httpResponse.body
        Assert.assertTrue(reservationCreationResponse.success)

        val createdReservation =
            reservationRepository.findById((reservationCreationResponse.payload as ReservationCreationPayload).bookingId)

        Assert.assertNotNull(createdReservation)
    }
}