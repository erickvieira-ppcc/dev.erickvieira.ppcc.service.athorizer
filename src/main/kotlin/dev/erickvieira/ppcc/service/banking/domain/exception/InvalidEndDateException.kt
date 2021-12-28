package dev.erickvieira.ppcc.service.banking.domain.exception

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

open class InvalidEndDateException : BadRequestException(
    message = "The endDate must be lower than or equal to the current date",
    result = BankingResult.invalidEndDate
)
