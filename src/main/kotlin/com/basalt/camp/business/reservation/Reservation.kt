package com.basalt.camp.business.reservation

import com.basalt.camp.business.user.User
import java.time.Instant
import java.util.UUID

class Reservation(val id: UUID,
                  val checkIn: Instant,
                  val checkOut: Instant,
                  val status: ReservationStatus = ReservationStatus.ACTIVE,
                  val owner: User,
                  val createdAt: Instant = Instant.now(),
                  val updatedAt: Instant = Instant.now())