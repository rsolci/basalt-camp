package com.basalt.camp.tests.integration

import com.basalt.camp.api.reservation.ReservationCreationPayload
import com.basalt.camp.api.reservation.ReservationResponse
import com.basalt.camp.api.reservation.VacancyPayload
import com.basalt.camp.api.reservation.VacancyResponse
import com.basalt.camp.base.BaseIntegrationTest
import com.basalt.camp.business.reservation.ReservationRepository
import com.basalt.camp.business.reservation.ReservationRestController
import com.basalt.camp.business.reservation.ReservationStatus
import com.basalt.camp.mocks.reservation.ReservationJsonRequest
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import org.apache.http.HttpStatus
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

class ReservationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @After
    fun cleanupReservations() {
        reservationRepository.deleteAll()
    }

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
    fun shouldBeAbleToCreateChainedReservations() {
        val httpReservation1 = createValidReservation(1, 3)
        val httpReservation2 = createValidReservation(3, 6)
        val httpReservation3 = createValidReservation(6, 7)

        Assert.assertEquals(HttpStatus.SC_OK, httpReservation1.status)
        Assert.assertEquals(HttpStatus.SC_OK, httpReservation2.status)
        Assert.assertEquals(HttpStatus.SC_OK, httpReservation3.status)


        val reservation1 = httpReservation1.body
        val reservation2= httpReservation1.body
        val reservation3 = httpReservation1.body
        Assert.assertTrue(reservation1.success)
        Assert.assertTrue(reservation2.success)
        Assert.assertTrue(reservation3.success)
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

    @Test
    fun vacancyTest() {
        createValidReservation(1, 3)
        createValidReservation(10, 13)
        createValidReservation(13, 16)

        val httpResponse = Unirest.get(apiUrl(ReservationRestController.PATH + ReservationRestController.VACANCY))
                .asObject(VacancyResponse::class.java)

        Assert.assertEquals(HttpStatus.SC_OK, httpResponse.status)
        val vacancyResponse = httpResponse.body
        Assert.assertTrue(vacancyResponse.success)

        val vacancyList = (vacancyResponse.payload as VacancyPayload).vacancyList
        Assert.assertEquals(3, vacancyList.size)

        Assert.assertEquals(LocalDate.now(), vacancyList[0].start)
        Assert.assertEquals(LocalDate.now().plusDays(1), vacancyList[0].end)

        Assert.assertEquals(LocalDate.now().plusDays(3), vacancyList[1].start)
        Assert.assertEquals(LocalDate.now().plusDays(10), vacancyList[1].end)

        Assert.assertEquals(LocalDate.now().plusDays(16), vacancyList[2].start)
        Assert.assertEquals(LocalDate.now().plusDays(30), vacancyList[2].end)
    }

    private fun createValidReservation(forwardStartDays: Long = 5, forwardEndDays: Long = 8): HttpResponse<ReservationResponse> {
        val creationRequest = createReservationRequest(forwardStartDays, forwardEndDays)

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