package com.basalt.camp.helper

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate

object DateFormatter {
    private val creationDateFormatter = SimpleDateFormat("yyyy-MM-dd")

    fun fromReservationCreation(dateStr: String): Instant {
        return creationDateFormatter.parse(dateStr).toInstant()
    }

    fun forReservationCreation(date: LocalDate): String {
        return creationDateFormatter.format(date.atTime(12, 0).atOffset(
            java.time.ZoneOffset.UTC).toInstant())
    }
}