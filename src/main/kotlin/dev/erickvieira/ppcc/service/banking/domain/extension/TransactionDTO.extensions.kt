package dev.erickvieira.ppcc.service.banking.domain.extension

import dev.erickvieira.ppcc.service.banking.web.api.model.TransactionDTO
import java.util.*

fun TransactionDTO.withOverriddenWalletId(walletId: UUID) = TransactionDTO(walletId = walletId, value = value)