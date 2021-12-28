package dev.erickvieira.ppcc.service.banking.domain.exception

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

open class InvalidDateIntervalException : BadRequestException(
    message = "The endDate parameter must be grater than the startDate one",
    result = BankingResult.invalidDateInterval
)
