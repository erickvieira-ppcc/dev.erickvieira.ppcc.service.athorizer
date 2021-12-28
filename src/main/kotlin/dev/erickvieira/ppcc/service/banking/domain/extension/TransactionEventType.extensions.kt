package dev.erickvieira.ppcc.service.banking.domain.extension

import dev.erickvieira.ppcc.service.banking.domain.model.TransactionEventType
import dev.erickvieira.ppcc.service.banking.domain.model.TransactionEventType.*
import dev.erickvieira.ppcc.service.banking.web.api.model.TransactionEvent

fun TransactionEventType.toTransactionEvent() = when (this) {
    PAYMENT -> TransactionEvent.payment
    INCOMING_BANK_TRANSFER -> TransactionEvent.incomingBankTransfer
    OUTGOING_BANK_TRANSFER -> TransactionEvent.outgoingBankTransfer
    WITHDRAWING -> TransactionEvent.withdrawing
    DEPOSIT -> TransactionEvent.deposit
    UNDO -> TransactionEvent.undo
}