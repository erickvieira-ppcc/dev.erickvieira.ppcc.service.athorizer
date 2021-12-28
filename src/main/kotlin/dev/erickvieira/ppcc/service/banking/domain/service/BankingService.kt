package dev.erickvieira.ppcc.service.banking.domain.service

import dev.erickvieira.ppcc.service.banking.domain.entity.Transaction
import dev.erickvieira.ppcc.service.banking.domain.exception.*
import dev.erickvieira.ppcc.service.banking.domain.exception.decline.*
import dev.erickvieira.ppcc.service.banking.domain.extension.*
import dev.erickvieira.ppcc.service.banking.domain.model.SearchDateInterval
import dev.erickvieira.ppcc.service.banking.domain.model.TransactionEventType
import dev.erickvieira.ppcc.service.banking.domain.model.TransactionEventType.*
import dev.erickvieira.ppcc.service.banking.domain.repository.TransactionRepository
import dev.erickvieira.ppcc.service.banking.domain.repository.WalletRepository
import dev.erickvieira.ppcc.service.banking.extension.*
import dev.erickvieira.ppcc.service.banking.web.api.BankingApiDelegate
import dev.erickvieira.ppcc.service.banking.web.api.model.*
import io.swagger.annotations.Api
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@Service
@Api(value = "Banking", description = "the Banking API", tags = ["Banking"])
class BankingService(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository
) : BankingApiDelegate {

    private val logger: Logger = LoggerFactory.getLogger(BankingService::class.java)

    @Throws(
        DecliningMinBalanceException::class,
        DecliningNullPayloadException::class,
        DecliningInvalidValueException::class,
        DecliningWalletNotFoundException::class,
        DecliningInsufficientFundsException::class,
        DecliningUnexpectedException::class,
    )
    override fun pay(
        transactionDTO: TransactionDTO?
    ): ResponseEntity<ProcessedTransactionDTO> = logger.executeOrLog(
        value = transactionDTO?.value,
        useDecliningVariant = true
    ) {
        transactionDTO.ensurePayloadNotNull(value = transactionDTO?.value) { payload ->
            tryToAuthorizeAtomicTransaction(payload = payload, event = PAYMENT) { transaction, result ->
                transactionRepository.save(transaction).toProcessedTransactionDTO(result = result)
            }.let { processedTransactionDTO -> ResponseEntity.ok(processedTransactionDTO) }
        }
    }

    @Throws(
        DecliningMinBalanceException::class,
        DecliningNullPayloadException::class,
        DecliningInvalidValueException::class,
        DecliningWalletNotFoundException::class,
        DecliningInsufficientFundsException::class,
        DecliningUnexpectedException::class,
    )
    override fun withdraw(
        transactionDTO: TransactionDTO?
    ): ResponseEntity<ProcessedTransactionDTO> = logger.executeOrLog(
        value = transactionDTO?.value,
        useDecliningVariant = true
    ) {
        transactionDTO.ensurePayloadNotNull(value = transactionDTO?.value) { payload ->
            tryToAuthorizeAtomicTransaction(payload = payload, event = WITHDRAWING) { transaction, result ->
                transactionRepository.save(transaction).toProcessedTransactionDTO(result = result)
            }.let { processedTransactionDTO -> ResponseEntity.ok(processedTransactionDTO) }
        }
    }

    @Throws(
        DecliningNullPayloadException::class,
        DecliningInvalidValueException::class,
        DecliningWalletNotFoundException::class,
        DecliningInsufficientFundsException::class,
        DecliningUnexpectedException::class,
    )
    override fun deposit(
        transactionDTO: TransactionDTO?
    ): ResponseEntity<ProcessedTransactionDTO> = logger.executeOrLog(
        value = transactionDTO?.value,
        useDecliningVariant = true
    ) {
        transactionDTO.ensurePayloadNotNull(value = transactionDTO?.value) { payload ->
            tryToAuthorizeAtomicTransaction(payload = payload, event = DEPOSIT) { transaction, result ->
                transactionRepository.save(transaction).toProcessedTransactionDTO(result = result)
            }.let { processedTransactionDTO -> ResponseEntity.ok(processedTransactionDTO) }
        }
    }

    @Throws(
        DecliningMinBalanceException::class,
        DecliningNullPayloadException::class,
        DecliningInvalidValueException::class,
        DecliningWalletNotFoundException::class,
        DecliningInsufficientFundsException::class,
        DecliningSameWalletTransferException::class,
        DecliningUnexpectedException::class,
    )
    override fun transferTo(
        fromWallet: UUID,
        transactionDTO: TransactionDTO?
    ): ResponseEntity<ProcessedTransactionDTO> = logger.executeOrLog(
        value = transactionDTO?.value,
        useDecliningVariant = true
    ) {
        transactionDTO.ensurePayloadNotNull(value = transactionDTO?.value) { to ->
            if (fromWallet == transactionDTO?.walletId) throw DecliningSameWalletTransferException(value = to.value)

            val from = to.withOverriddenWalletId(walletId = fromWallet)
            var incomingTransaction = Transaction()
            var outgoingTransaction = Transaction()
            var transferResult = BankingResult.declinedByForbiddenOperation

            tryToAuthorizeAtomicTransaction(
                payload = to,
                event = INCOMING_BANK_TRANSFER
            ) { transaction, result ->
                incomingTransaction = transaction
                transferResult = result
                null
            }

            if (transferResult == BankingResult.declinedByForbiddenOperation) {
                return@executeOrLog ResponseEntity.ok(
                    incomingTransaction.toProcessedTransactionDTO(result = transferResult)
                )
            }

            tryToAuthorizeAtomicTransaction(
                payload = from,
                event = OUTGOING_BANK_TRANSFER
            ) { transaction, result ->
                outgoingTransaction = transaction
                transferResult = result
                null
            }

            transactionRepository.save(incomingTransaction)
            transactionRepository.save(outgoingTransaction).let {
                ResponseEntity.ok(outgoingTransaction.toProcessedTransactionDTO(result = transferResult))
            }
        }
    }

    @Throws(
        DecliningProhibedUndoException::class,
        DecliningWalletNotFoundException::class,
        DecliningTransactionNotFoundException::class,
        DecliningUnexpectedException::class,
    )
    override fun undo(transactionId: UUID): ResponseEntity<ProcessedTransactionDTO> = logger.executeOrLog(
        useDecliningVariant = true
    ) {
        transactionRepository.findFirstById(transactionId)?.let { undesiredTransaction ->
            if (undesiredTransaction.event != PAYMENT) throw DecliningProhibedUndoException(value = 0.0)
            tryToAuthorizeAtomicTransaction(
                payload = undesiredTransaction.toTransactionDTO(),
                event = UNDO
            ) { transaction, result ->
                transactionRepository.save(transaction).toProcessedTransactionDTO(result = result)
            }.let { processedTransactionDTO -> ResponseEntity.ok(processedTransactionDTO) }
        } ?: throw DecliningTransactionNotFoundException(value = 0.0)
    }

    @Throws(
        WalletNotFoundException::class,
        UnexpectedException::class,
    )
    override fun balance(walletId: UUID): ResponseEntity<BalanceDTO> = logger.executeOrLog {
        val method = "method" to "balance"
        logger.custom.info(method, "walletId" to walletId)
        walletRepository.findFirstByIdAndDeletedAtIsNull(id = walletId)
            ?: throw WalletNotFoundException("id" to walletId)

        (transactionRepository.sumCreditDeltaByWalletId(walletId = walletId) ?: BigDecimal.ZERO).let { balance ->
            logger.custom.info(method, "balance" to balance)
            ResponseEntity.ok(BalanceDTO(walletId = walletId, balance = balance.toDouble()))
        }
    }

    @Throws(
        WalletNotFoundException::class,
        InvalidDateIntervalException::class,
        InvalidEndDateException::class,
        InvalidStartDateException::class,
        TransactionNotFoundException::class,
        UnexpectedException::class,
    )
    override fun history(
        walletId: UUID,
        startDate: LocalDate?,
        endDate: LocalDate?,
        page: Int,
        size: Int
    ): ResponseEntity<PageApprovedTransactionDTO> = logger.executeOrLog {
        val method = "method" to "history"
        val search = arrayOf(
            "walletId" to walletId,
            "startDate" to startDate,
            "endDate" to endDate,
            "page" to page,
            "size" to size
        )
        logger.custom.info(method, *search)

        val interval = SearchDateInterval.fromUnsafeParameters(startDate = startDate, endDate = endDate)
        val pageable = PageRequest(pagination = search.toMap())
        walletRepository.findFirstByIdAndDeletedAtIsNull(walletId) ?: throw WalletNotFoundException(search = search)
        val result = transactionRepository.findAllByWalletIdAndCreatedAtBetweenAndCreditDeltaNotOrderByCreatedAtDesc(
            walletId = walletId,
            startDate = interval.start,
            endDate = interval.end,
            pageable = pageable
        )

        logger.custom.info(method, "totalElements" to result.totalElements, "totalPages" to result.totalPages)

        if (result.isEmpty) throw TransactionNotFoundException(search = search)

        ResponseEntity.ok(load { fromPage(page = result) })
    }

    @Throws(
        DecliningMinBalanceException::class,
        DecliningNullPayloadException::class,
        DecliningInvalidValueException::class,
        DecliningWalletNotFoundException::class,
        DecliningInsufficientFundsException::class,
    )
    private fun tryToAuthorizeAtomicTransaction(
        payload: TransactionDTO,
        event: TransactionEventType,
        callback: (transaction: Transaction, result: BankingResult) -> ProcessedTransactionDTO?
    ): ProcessedTransactionDTO? {
        val method = "method" to when (event) {
            PAYMENT -> "pay"
            INCOMING_BANK_TRANSFER -> "transfer"
            OUTGOING_BANK_TRANSFER -> "transfer"
            WITHDRAWING -> "withdraw"
            DEPOSIT -> "deposit"
            UNDO -> "undo"
        }
        if (payload.value <= 0) throw DecliningInvalidValueException(value = payload.value)
        val value = payload.value.toBigDecimal()
        logger.custom.info(method, *payload.toPairArray())
        val wallet = findWalletCandidate(userId = payload.userId, walletId = payload.walletId, value = payload.value)
        logger.custom.info(method, *wallet.toPairArray())
        val balance = transactionRepository.sumCreditDeltaByWalletId(walletId = wallet.id) ?: BigDecimal.ZERO
        logger.custom.info(method, "balance" to balance)
        val canAuthorize = when (event) {
            DEPOSIT -> true
            INCOMING_BANK_TRANSFER -> true
            UNDO -> true
            else -> if (value > balance) {
                throw DecliningInsufficientFundsException(value = payload.value)
            } else if (wallet.minBalance.isBetween(start = BigDecimal.ZERO, end = balance - value)) {
                throw DecliningMinBalanceException(value = payload.value)
            } else true
        } && wallet.isPermittedBySettings(event = event)

        val (transaction, result) = if (canAuthorize) {
            logger.custom.info("approved" to value)
            wallet.approve(value = payload.value, event = event) to BankingResult.approved
        } else wallet.decline(value = payload.value, event = event) to BankingResult.declinedByForbiddenOperation

        return callback(transaction, result)
    }

    @Throws(
        DecliningWalletNotFoundException::class,
    )
    private fun findWalletCandidate(
        userId: UUID? = null,
        walletId: UUID? = null,
        value: Double
    ) = with(walletRepository) {
        if (userId == null && walletId == null) throw DecliningWalletNotFoundException(value = value)
        when (userId) {
            null -> findFirstByIdAndDeletedAtIsNull(id = walletId)
            else -> findFirstByUserIdAndIsDefaultIsTrueAndDeletedAtIsNull(userId = userId)
        } ?: throw DecliningWalletNotFoundException(value = value)
    }

    @Throws(
        DecliningNullPayloadException::class,
    )
    private inline fun <T : Any, S : Any> T?.ensurePayloadNotNull(
        value: Double?,
        payload: String = TransactionDTO::class.java.name,
        callback: (it: T) -> S
    ) = this?.let {
        callback(it)
    } ?: throw DecliningNullPayloadException(payload = payload, value = value ?: 0.0)

}