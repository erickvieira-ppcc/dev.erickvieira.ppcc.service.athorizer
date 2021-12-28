package dev.erickvieira.ppcc.service.banking.domain.exception

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

class TransactionNotFoundException(vararg search: Pair<String, Any?>) : NotFoundException(
    message = "No transactions found using the search terms provided",
    result = BankingResult.transactionNotFound,
    search = search
)