package dev.erickvieira.ppcc.service.banking.domain.exception.decline

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

open class DecliningInvalidValueException(value: Double) : DecliningBadRequestException(
    message = "The value must be grater than zero",
    value = value,
    result = BankingResult.declinedByInvalidValue
)
