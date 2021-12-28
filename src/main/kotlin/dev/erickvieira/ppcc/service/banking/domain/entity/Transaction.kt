package dev.erickvieira.ppcc.service.banking.domain.entity

import com.google.gson.Gson
import dev.erickvieira.ppcc.service.banking.domain.model.TransactionEventType
import org.hibernate.Hibernate
import org.hibernate.annotations.GenericGenerator
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*

@Table(name = "tb_transaction")
@Entity
data class Transaction(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID? = null,

    @Column(name = "original_id", nullable = true, updatable = false)
    val originalId: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id", nullable = true, updatable = false)
    val wallet: Wallet? = null,

    @Enumerated
    @Column(name = "event", nullable = false, columnDefinition = "smallint", updatable = false)
    val event: TransactionEventType? = null,

    @Column(name = "value", nullable = false, updatable = false)
    val value: BigDecimal = BigDecimal.ZERO,

    @Column(name = "credit_delta", nullable = false, updatable = false)
    val creditDelta: BigDecimal = BigDecimal.ZERO,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: OffsetDateTime? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Wallet

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String = Gson().toJson(this)

    companion object {}
}