package dev.erickvieira.ppcc.service.banking.domain.exception

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

open class BadRequestException(message: String, result: BankingResult) :
    BaseException(message = message, result = result)
