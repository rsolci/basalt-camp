package com.basalt.camp.reservation

import com.basalt.camp.user.User
import java.time.Instant
import java.util.UUID

class Reservation(val id: UUID,
                  val checkIn: Instant,
                  val checkOut: Instant,
                  val status: ReservationStatus,
                  val owner: User,
                  val createdAt: Instant = Instant.now(),
                  val updatedAt: Instant = Instant.now())