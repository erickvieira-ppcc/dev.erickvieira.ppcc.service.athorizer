package dev.erickvieira.ppcc.service.banking.domain.extension

import dev.erickvieira.ppcc.service.banking.domain.exception.InvalidDateIntervalException
import dev.erickvieira.ppcc.service.banking.domain.exception.InvalidEndDateException
import dev.erickvieira.ppcc.service.banking.domain.exception.InvalidStartDateException
import dev.erickvieira.ppcc.service.banking.domain.model.SearchDateInterval
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.jvm.Throws

@Throws(
    InvalidDateIntervalException::class,
    InvalidEndDateException::class,
    InvalidStartDateException::class,
)
fun SearchDateInterval.Companion.fromUnsafeParameters(startDate: LocalDate?, endDate: LocalDate?): SearchDateInterval {
    val safeEndDate = endDate ?: LocalDate.now()
    val safeStartDate = startDate ?: safeEndDate.minusDays(90)
    val today = LocalDate.now().atStartOfDay().toLocalDate()

    if (safeStartDate.isAfter(safeEndDate)) throw InvalidDateIntervalException()
    if (safeEndDate.isAfter(today)) throw InvalidEndDateException()
    if (safeStartDate.isBefore(safeEndDate.minusDays(90))) throw InvalidStartDateException()

    return SearchDateInterval(
        start = safeStartDate.atStartOfDay().atOffset(ZoneOffset.UTC),
        end = safeEndDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC)
    )
}