package dev.erickvieira.ppcc.service.banking.domain.exception

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

open class InvalidStartDateException : BadRequestException(
    message = "The startDate must be grater than or equal to the endDate minus 90 days",
    result = BankingResult.invalidStartDate
)
