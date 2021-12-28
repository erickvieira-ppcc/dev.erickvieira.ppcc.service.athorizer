package dev.erickvieira.ppcc.service.banking.domain.exception.decline

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

class DecliningSameWalletTransferException(value: Double) : DecliningBadRequestException(
    message = "Aborting: same wallet transfer",
    value = value,
    result = BankingResult.declinedBySameWalletTransfer,
)