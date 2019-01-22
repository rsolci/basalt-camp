package com.basalt.camp.api.reservation

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import java.time.LocalDate

class VacancyItem(@JsonDeserialize(using = LocalDateDeserializer::class) val start: LocalDate,
                  @JsonDeserialize(using = LocalDateDeserializer::class) val end: LocalDate)