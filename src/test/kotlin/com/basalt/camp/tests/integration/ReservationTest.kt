package com.basalt.camp.tests.integration

import com.basalt.camp.api.reservation.ReservationCreationPayload
import com.basalt.camp.api.reservation.ReservationResponse
import com.basalt.camp.base.BaseIntegrationTest
import com.basalt.camp.business.reservation.ReservationRepository
import com.basalt.camp.business.reservation.ReservationRestController
import com.basalt.camp.business.reservation.ReservationStatus
import com.basalt.camp.mocks.reservation.ReservationJsonRequest
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import org.apache.http.HttpStatus
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

// FIXME this fixes database dirty between tests, but makes extremely slow
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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

        val editRequest = createReservationRequest(5, 8)

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

    @Test
    fun canNotCreateOverlappingToFutureReservations() {
        createValidReservation()

        val creationRequest = createReservationRequest(6, 9)
        val httpResponse = Unirest.post(apiUrl(ReservationRestController.PATH)).body(creationRequest)
                .asObject(ReservationResponse::class.java)

        Assert.assertEquals(HttpStatus.SC_OK, httpResponse.status)
        val reservationCreationResponse = httpResponse.body
        Assert.assertFalse(reservationCreationResponse.success)
    }

    @Test
    fun canNotCreateOverlappingToPastReservations() {
        createValidReservation()

        val creationRequest = createReservationRequest(4, 7)
        val httpResponse = Unirest.post(apiUrl(ReservationRestController.PATH)).body(creationRequest)
                .asObject(ReservationResponse::class.java)

        Assert.assertEquals(HttpStatus.SC_OK, httpResponse.status)
        val reservationCreationResponse = httpResponse.body
        Assert.assertFalse(reservationCreationResponse.success)
    }

    @Test
    fun canNotCreateOverlappingToMiddleReservations() {
        createValidReservation()

        val creationRequest = createReservationRequest(6, 7)
        val httpResponse = Unirest.post(apiUrl(ReservationRestController.PATH)).body(creationRequest)
                .asObject(ReservationResponse::class.java)

        Assert.assertEquals(HttpStatus.SC_OK, httpResponse.status)
        val reservationCreationResponse = httpResponse.body
        Assert.assertFalse(reservationCreationResponse.success)
    }

    @Test
    fun cancellingReservation() {
        val httpResponse =
                createValidReservation()

        val bookingId = (httpResponse.body.payload as ReservationCreationPayload).bookingId

        val httpDelete = Unirest.delete(apiUrl(ReservationRestController.PATH + ReservationRestController.ID))
                .routeParam("id", bookingId.toString()).asObject(ReservationResponse::class.java)
        Assert.assertEquals(HttpStatus.SC_OK, httpDelete.status)
        val reservationResponse = httpDelete.body
        Assert.assertTrue(reservationResponse.success)

        val cancelledReservation =
                reservationRepository.findById(bookingId)
        Assert.assertEquals(ReservationStatus.CANCELLED, cancelledReservation.get().status)
    }

    private fun createValidReservation(): HttpResponse<ReservationResponse> {
        val creationRequest = createReservationRequest(5, 8)

        return Unirest.post(apiUrl(ReservationRestController.PATH)).body(creationRequest)
                .asObject(ReservationResponse::class.java)
    }

    private fun createReservationRequest(forwardStartDays: Long, forwardEndDays: Long): ReservationJsonRequest {
        return ReservationJsonRequest(
                email = "test@test.com",
                name = "Good name",
                checkIn = LocalDate.now().plusDays(forwardStartDays).toString(),
                checkOut = LocalDate.now().plusDays(forwardEndDays).toString()
        )
    }
}