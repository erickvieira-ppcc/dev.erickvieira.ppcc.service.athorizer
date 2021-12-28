package dev.erickvieira.ppcc.service.banking.domain.exception.decline

import dev.erickvieira.ppcc.service.banking.web.api.model.BankingResult

class DecliningProhibedUndoException(value: Double) : DecliningBadRequestException(
    message = "It is impossible to undo anything else but payments",
    value = value,
    result = BankingResult.declinedByProhibedUndo,
)