package dev.erickvieira.ppcc.service.banking.domain.exception.decline

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

class DecliningWalletNotFoundException(value: Double, vararg search: Pair<String, Any?>) : DecliningNotFoundException(
    message = "No wallets found using the search terms provided",
    value = value,
    search = search,
    result = BankingResult.declinedByWalletNotFound,
)