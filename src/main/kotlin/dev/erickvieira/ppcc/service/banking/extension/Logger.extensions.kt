package dev.erickvieira.ppcc.service.banking.extension

import dev.erickvieira.ppcc.service.banking.domain.exception.BaseException
import dev.erickvieira.ppcc.service.banking.domain.exception.UnexpectedException
import dev.erickvieira.ppcc.service.banking.domain.exception.decline.DecliningUnexpectedException
import dev.erickvieira.ppcc.service.banking.domain.exception.decline.DecliningException
import org.slf4j.Logger

interface CustomLogger {
    fun debug(vararg logs: Pair<String, Any?>)
    fun trace(vararg logs: Pair<String, Any?>)
    fun info(vararg logs: Pair<String, Any?>)
    fun warn(vararg logs: Pair<String, Any?>)
    fun error(vararg logs: Pair<String, Any?>)
    fun debug(msg: String?)
    fun trace(msg: String?)
    fun info(msg: String?)
    fun warn(msg: String?)
    fun error(msg: String?)
}

val Logger.custom: CustomLogger
    get() {
        val logger = this
        val identifier = "BANKING-SERVICE"
        val prefix = "<< $identifier >>"

        fun buildMessage(vararg logs: Pair<String, Any?>) =
            logs.fold(prefix) { msg, log -> "$msg | ${log.first}: ${log.second}" }

        return object : CustomLogger {
            override fun debug(vararg logs: Pair<String, Any?>) = logger.debug(buildMessage(*logs))
            override fun trace(vararg logs: Pair<String, Any?>) = logger.trace(buildMessage(*logs))
            override fun info(vararg logs: Pair<String, Any?>) = logger.info(buildMessage(*logs))
            override fun warn(vararg logs: Pair<String, Any?>) = logger.warn(buildMessage(*logs))
            override fun error(vararg logs: Pair<String, Any?>) = logger.error(buildMessage(*logs))
            override fun debug(msg: String?): Unit = logger.debug("$prefix $msg")
            override fun trace(msg: String?): Unit = logger.trace("$prefix $msg")
            override fun info(msg: String?): Unit = logger.info("$prefix $msg")
            override fun warn(msg: String?): Unit = logger.warn("$prefix $msg")
            override fun error(msg: String?): Unit = logger.error("$prefix $msg")
        }
    }

@Throws(
    BaseException::class,
    DecliningException::class,
    DecliningUnexpectedException::class,
    UnexpectedException::class
)
inline fun <T> Logger.executeOrLog(
    value: Double? = 0.0,
    useDecliningVariant: Boolean = false,
    callback: () -> T
) = try {
    callback()
} catch (e: BaseException) {
    this.custom.error(e::class.java.name to e.message)
    throw e
} catch (e: DecliningException) {
    this.custom.error(e::class.java.name to e.message)
    throw e
} catch (e: Exception) {
    this.custom.error("UnmappedException" to e.message)
    if (useDecliningVariant) throw DecliningUnexpectedException(message = e.message, value = value)
    throw UnexpectedException(e.message)
} catch (e: Error) {
    this.custom.error("UnmappedError" to e.message)
    if (useDecliningVariant) throw DecliningUnexpectedException(message = e.message, value = value)
    throw UnexpectedException(e.message)
}