package dev.erickvieira.ppcc.service.banking.domain.exception.decline

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

class DecliningForbiddenOperationException(value: Double) : DecliningBadRequestException(
    message = "Forbidden operation for the given wallet",
    value = value,
    result = BankingResult.declinedByForbiddenOperation,
)