package dev.erickvieira.ppcc.service.banking.domain.exception

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

open class UnexpectedException(message: String?) : BaseException(
    message = message ?: "Unexpected Error",
    result = BankingResult.unexpectedError
)
