package com.basalt.camp.tests.integration

import com.basalt.camp.api.reservation.ReservationCreationPayload
import com.basalt.camp.api.reservation.ReservationResponse
import com.basalt.camp.base.BaseIntegrationTest
import com.basalt.camp.business.reservation.ReservationRepository
import com.basalt.camp.business.reservation.ReservationRestController
import com.basalt.camp.mocks.reservation.ReservationJsonRequest
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import org.apache.http.HttpStatus
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

class ReservationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @Test
    fun shouldCreateReservation() {
        val httpResponse =
                createValidReservation()
        Assert.assertEquals(HttpStatus.SC_OK, httpResponse.status)
        val reservationCreationResponse = httpResponse.body
        Assert.assertTrue(reservationCreationResponse.success)

        val createdReservation =
                reservationRepository.findById((reservationCreationResponse.payload as ReservationCreationPayload).bookingId)

        Assert.assertTrue(createdReservation.isPresent)
    }

    @Test
    fun shouldEditReservation() {
        val httpResponse =
                createValidReservation()

        val bookingId = (httpResponse.body.payload as ReservationCreationPayload).bookingId

        val editRequest = ReservationJsonRequest(
                email = "test@test.com",
                name = "Good name",
                checkIn = LocalDate.now().plusDays(5).toString(),
                checkOut = LocalDate.now().plusDays(8).toString()
        )

        val httpEditResponse = Unirest.put(apiUrl(ReservationRestController.PATH + ReservationRestController.ID))
                .routeParam("id", bookingId.toString()).body(editRequest).asObject(ReservationResponse::class.java)

        Assert.assertEquals(HttpStatus.SC_OK, httpEditResponse.status)
        val reservationEditResponse = httpResponse.body
        Assert.assertTrue(reservationEditResponse.success)

        val reservation =
                reservationRepository.findById((reservationEditResponse.payload as ReservationCreationPayload).bookingId)

        Assert.assertEquals(4, Duration.between(Instant.now(), reservation.get().checkIn).toDays())
        Assert.assertEquals(7, Duration.between(Instant.now(), reservation.get().checkOut).toDays())
    }

    private fun createValidReservation(): HttpResponse<ReservationResponse> {
        val creationRequest = ReservationJsonRequest(
                email = "test@test.com",
                name = "Good name",
                checkIn = LocalDate.now().plusDays(1).toString(),
                checkOut = LocalDate.now().plusDays(3).toString()
        )

        return Unirest.post(apiUrl(ReservationRestController.PATH)).body(creationRequest)
                .asObject(ReservationResponse::class.java)
    }
}