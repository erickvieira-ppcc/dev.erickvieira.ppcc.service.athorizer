package dev.erickvieira.ppcc.service.banking.domain.exception

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

open class BaseException constructor(
    @Transient override var message: String,
    val result: BankingResult? = null
) : RuntimeException()
