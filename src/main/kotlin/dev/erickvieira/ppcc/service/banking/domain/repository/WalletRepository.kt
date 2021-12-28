package dev.erickvieira.ppcc.service.banking.domain.repository

import dev.erickvieira.ppcc.service.banking.domain.entity.Wallet
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface WalletRepository : JpaRepository<Wallet, UUID> {

    fun findFirstByIdAndDeletedAtIsNull(id: UUID?): Wallet?

    @Suppress("SpringDataMethodInconsistencyInspection")
    fun findFirstByUserIdAndIsDefaultIsTrueAndDeletedAtIsNull(userId: UUID?): Wallet?

}