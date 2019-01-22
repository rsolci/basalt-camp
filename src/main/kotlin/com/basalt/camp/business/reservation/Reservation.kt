package com.basalt.camp.business.reservation

import com.basalt.camp.business.user.User
import java.time.Instant
import java.util.UUID
import javax.persistence.*

@Entity
class Reservation(@Id val id: UUID,
                  val checkIn: Instant,
                  val checkOut: Instant,
                  @Enumerated(EnumType.STRING) val status: ReservationStatus = ReservationStatus.ACTIVE,
                  @ManyToOne val owner: User,
                  val createdAt: Instant = Instant.now(),
                  val updatedAt: Instant = Instant.now())