package dev.erickvieira.ppcc.service.banking.domain.exception.decline

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

open class DecliningException constructor(
    @Transient override var message: String,
    val value: Double = 0.0,
    val result: BankingResult? = null
) : RuntimeException()
