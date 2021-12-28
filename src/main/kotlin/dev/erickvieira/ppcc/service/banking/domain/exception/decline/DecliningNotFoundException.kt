package dev.erickvieira.ppcc.service.banking.domain.exception.decline

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

open class DecliningNotFoundException(
    message: String,
    value: Double,
    result: BankingResult,
    vararg search: Pair<String, Any?>
) : DecliningException(
    message = "$message${
        search
            .filter { it.second != null }
            .takeUnless { it.isEmpty() }
            ?.joinToString(prefix = " - ", separator = ", ") { "${it.first}: ${it.second}" }
            ?: String()
    }",
    value = value,
    result = result
)