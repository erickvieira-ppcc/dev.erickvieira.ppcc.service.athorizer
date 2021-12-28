package dev.erickvieira.ppcc.service.banking.domain.extension

import dev.erickvieira.ppcc.service.banking.domain.entity.Transaction
import dev.erickvieira.ppcc.service.banking.domain.entity.Wallet
import dev.erickvieira.ppcc.service.banking.domain.model.TransactionEventType
import dev.erickvieira.ppcc.service.banking.domain.model.TransactionEventType.*
import dev.erickvieira.ppcc.service.banking.web.api.model.WalletDTO
import java.math.BigDecimal
import java.time.OffsetDateTime

fun Wallet.toWalletDTO() = WalletDTO(
    id = id!!,
    surname = surname
)

fun Wallet.approve(value: Double, event: TransactionEventType) = value.toBigDecimal().let {
    fun BigDecimal.delta(): BigDecimal = when(event) {
        PAYMENT -> invert()
        OUTGOING_BANK_TRANSFER -> invert()
        WITHDRAWING -> invert()
        else -> this
    }
    Transaction(
        wallet = this,
        event = event,
        value = it,
        creditDelta = it.delta(),
        createdAt = OffsetDateTime.now()
    )
}

fun Wallet.decline(value: Double, event: TransactionEventType) = Transaction(
    wallet = this,
    event = event,
    value = value.toBigDecimal(),
    createdAt = OffsetDateTime.now()
)

fun Wallet.isPermittedBySettings(event: TransactionEventType) = when (event) {
    PAYMENT -> acceptPayments
    INCOMING_BANK_TRANSFER -> acceptBankTransfer
    OUTGOING_BANK_TRANSFER -> acceptBankTransfer
    WITHDRAWING -> acceptWithdrawing
    DEPOSIT -> acceptDeposit
    else -> true
} && isActive