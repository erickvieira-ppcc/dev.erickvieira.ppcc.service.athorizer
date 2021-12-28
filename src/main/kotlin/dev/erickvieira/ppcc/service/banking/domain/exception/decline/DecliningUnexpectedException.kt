package dev.erickvieira.ppcc.service.banking.domain.exception.decline

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

open class DecliningUnexpectedException(message: String?, value: Double?) : DecliningException(
    message = message ?: "Unexpected Error",
    value = value ?: 0.0,
    result = BankingResult.declinedByUnexpectedError
)
