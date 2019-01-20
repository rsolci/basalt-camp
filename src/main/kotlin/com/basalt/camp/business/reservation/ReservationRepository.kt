package com.basalt.camp.business.reservation

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ReservationRepository: JpaRepository<Reservation, UUID> {
}