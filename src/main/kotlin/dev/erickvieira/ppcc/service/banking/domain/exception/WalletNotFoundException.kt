package dev.erickvieira.ppcc.service.banking.domain.exception

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

class WalletNotFoundException(vararg search: Pair<String, Any?>) : NotFoundException(
    message = "No wallets found using the search terms provided",
    result = BankingResult.walletNotFound,
    search = search
)
