package dev.erickvieira.ppcc.service.banking.extension

import dev.erickvieira.ppcc.service.banking.web.api.model.Direction
import dev.erickvieira.ppcc.service.banking.web.api.model.TransactionFields
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

fun PageRequest(
    pagination: Map<String, Any?>
): PageRequest = PageRequest.of(
    pagination["page"] as Int? ?: 0,
    pagination["size"] as Int? ?: 20,
    Sort
        .by((pagination["sort"] as TransactionFields? ?: TransactionFields.createdAt).value)
        .let { if (pagination["direction"] == Direction.asc) it.ascending() else it.descending() }
)