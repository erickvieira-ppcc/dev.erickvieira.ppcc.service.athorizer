package dev.erickvieira.ppcc.service.banking.domain.repository

import dev.erickvieira.ppcc.service.banking.domain.entity.Transaction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

interface TransactionRepository : JpaRepository<Transaction, UUID> {

    @Query("SELECT SUM(t.creditDelta) FROM Transaction t WHERE t.wallet.id = ?1")
    fun sumCreditDeltaByWalletId(walletId: UUID?): BigDecimal?

    fun findAllByWalletIdAndCreatedAtBetweenAndCreditDeltaNotOrderByCreatedAtDesc(
        walletId: UUID,
        startDate: OffsetDateTime,
        endDate: OffsetDateTime,
        creditDelta: BigDecimal = BigDecimal.ZERO,
        pageable: Pageable
    ): Page<Transaction>

    fun findFirstById(id: UUID?): Transaction?

}