package dev.erickvieira.ppcc.service.banking.domain.exception

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

open class NotFoundException(
    message: String,
    result: BankingResult,
    vararg search: Pair<String, Any?>
) : BaseException(
    message = "$message${
        search
            .filter { it.second != null }
            .takeUnless { it.isEmpty() }
            ?.joinToString(prefix = " - ", separator = ", ") { "${it.first}: ${it.second}" }
            ?: String()
    }",
    result = result
)