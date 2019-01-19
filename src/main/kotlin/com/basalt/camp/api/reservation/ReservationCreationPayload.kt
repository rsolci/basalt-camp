package com.basalt.camp.api.reservation

import com.basalt.camp.api.BasePayload
import java.util.UUID

class ReservationCreationPayload(val bookingId: UUID) : BasePayload()