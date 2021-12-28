package dev.erickvieira.ppcc.service.banking.domain.extension

import dev.erickvieira.ppcc.service.banking.domain.entity.Transaction
import dev.erickvieira.ppcc.service.banking.web.api.model.PageApprovedTransactionDTO
import org.springframework.data.domain.Page

fun PageApprovedTransactionDTO?.fromPage(page: Page<Transaction>) = PageApprovedTransactionDTO(
    pageable = page.pageable,
    content = page.content.map { it.toApprovedTransactionDTO() },
    total = page.totalElements,
    pageCount = page.totalPages,
    sortedBy = page.pageable.sort.toString()
)