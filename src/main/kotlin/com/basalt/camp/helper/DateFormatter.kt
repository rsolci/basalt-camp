package com.basalt.camp.helper

import java.text.SimpleDateFormat
import java.time.Instant

object DateFormatter {
    private val creationDateFormatter = SimpleDateFormat("YYYY-MM-YY")

    fun fromReservationCreation(dateStr: String): Instant {
        return creationDateFormatter.parse(dateStr).toInstant()
    }
}