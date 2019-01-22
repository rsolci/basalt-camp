package com.basalt.camp.business.reservation

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface ReservationRepository : JpaRepository<Reservation, UUID> {

    @Query("SELECT r FROM Reservation r WHERE r.id <> :id AND (:end > r.checkIn  AND (:end < r.checkOut OR :start < r.checkOut))")
    fun findOtherReservationsWithinPeriod(@Param("id") reservationId: UUID,
                                          @Param("start") start: Instant,
                                          @Param("end") end: Instant): List<Reservation>
}