package dev.erickvieira.ppcc.service.banking.domain.exception.decline

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

open class DecliningBadRequestException(
    message: String,
    value: Double,
    result: BankingResult,
) : DecliningException(message = message, value = value, result = result)
