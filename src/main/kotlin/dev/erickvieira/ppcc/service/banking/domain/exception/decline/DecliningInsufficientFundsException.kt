package dev.erickvieira.ppcc.service.banking.domain.exception.decline

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

class DecliningInsufficientFundsException(value: Double) : DecliningBadRequestException(
    message = "Insufficient funds",
    value = value,
    result = BankingResult.declinedByInsufficientFunds,
)