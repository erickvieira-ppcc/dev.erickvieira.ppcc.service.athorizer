package dev.erickvieira.ppcc.service.banking.domain.exception.decline

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

class DecliningMinBalanceException(payload: String? = null, value: Double) : DecliningBadRequestException(
    message = "The${payload?.let { it.ifBlank { " " } } ?: " $payload "}payload can't be null",
    value = value,
    result = BankingResult.declinedByMinBalance
)