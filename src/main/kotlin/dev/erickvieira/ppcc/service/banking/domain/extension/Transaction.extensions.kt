package dev.erickvieira.ppcc.service.banking.domain.extension

import dev.erickvieira.ppcc.service.banking.domain.entity.Transaction
import dev.erickvieira.ppcc.service.banking.web.api.model.ApprovedTransactionDTO
import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult
import dev.erickvieira.ppcc.service.banking.web.api.model.ProcessedTransactionDTO
import dev.erickvieira.ppcc.service.banking.web.api.model.TransactionDTO

fun Transaction.toProcessedTransactionDTO(result: BankingResult) = ProcessedTransactionDTO(
    id = id!!,
    value = value.toDouble(),
    walletId = wallet!!.id!!,
    approvedValue = creditDelta.modulus().toDouble(),
    result = result,
    timestamp = createdAt!!
)

fun Transaction.toTransactionDTO() = TransactionDTO(
    value = value.toDouble(),
    walletId = wallet!!.id
)

fun Transaction.toApprovedTransactionDTO() = ApprovedTransactionDTO(
    id = id!!,
    originalId = originalId,
    wallet = wallet!!.toWalletDTO(),
    event = event!!.toTransactionEvent(),
    value = value.toDouble(),
    creditDelta = creditDelta.toDouble(),
    createdAt = createdAt!!,
)