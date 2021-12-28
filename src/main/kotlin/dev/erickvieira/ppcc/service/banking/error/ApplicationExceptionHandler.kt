package dev.erickvieira.ppcc.service.banking.error

import dev.erickvieira.ppcc.service.banking.domain.exception.BadRequestException
import dev.erickvieira.ppcc.service.banking.domain.exception.UnexpectedException
import dev.erickvieira.ppcc.service.banking.domain.exception.NotFoundException
import dev.erickvieira.ppcc.service.banking.domain.exception.decline.DecliningBadRequestException
import dev.erickvieira.ppcc.service.banking.domain.exception.decline.DecliningNotFoundException
import dev.erickvieira.ppcc.service.banking.domain.exception.decline.DecliningUnexpectedException
import dev.erickvieira.ppcc.service.banking.web.api.model.ApiError
import dev.erickvieira.ppcc.service.banking.web.api.model.ProcessedTransactionDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.OffsetDateTime

@ControllerAdvice
class ApplicationExceptionHandler {

    @ExceptionHandler(DecliningBadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun exception(ex: DecliningBadRequestException) = ProcessedTransactionDTO(
        value = ex.value,
        result = ex.result,
        message = ex.message,
        timestamp = OffsetDateTime.now()
    )

    @ExceptionHandler(DecliningNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun exception(ex: DecliningNotFoundException) = ProcessedTransactionDTO(
        value = ex.value,
        result = ex.result,
        message = ex.message,
        timestamp = OffsetDateTime.now()
    )

    @ExceptionHandler(DecliningUnexpectedException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun exception(ex: DecliningUnexpectedException) = ProcessedTransactionDTO(
        value = ex.value,
        result = ex.result,
        message = ex.message,
        timestamp = OffsetDateTime.now()
    )

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun exception(ex: NotFoundException) = ApiError(type = ex.result, message = ex.message)

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun exception(ex: BadRequestException) = ApiError(type = ex.result, message = ex.message)

    @ExceptionHandler(UnexpectedException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun exception(ex: UnexpectedException) = ApiError(type = ex.result, message = ex.message)

}