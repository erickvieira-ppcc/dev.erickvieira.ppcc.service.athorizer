package dev.erickvieira.ppcc.service.banking.domain.model

enum class TransactionEventType {
    PAYMENT,
    INCOMING_BANK_TRANSFER,
    OUTGOING_BANK_TRANSFER,
    WITHDRAWING,
    DEPOSIT,
    UNDO
}