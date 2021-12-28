package dev.erickvieira.ppcc.service.banking.domain.model

import java.time.OffsetDateTime

data class SearchDateInterval(
    val start: OffsetDateTime,
    val end: OffsetDateTime
) {
    companion object {}
}
