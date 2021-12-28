package dev.erickvieira.ppcc.service.banking.integrated

import dev.erickvieira.ppcc.service.banking.web.api.model.PageApprovedTransactionDTO
import dev.erickvieira.ppcc.service.banking.web.api.model.TransactionDTO
import io.restassured.RestAssured
import io.restassured.response.ResponseBodyExtractionOptions
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("test")
open class BankingServiceIntegratedTests {

    private fun Map<String, Any?>.asQueryString() = keys.joinToString("&") { key -> "$key=${this[key]}" }

    protected fun makeRequest(
        path: String = "",
        query: Map<String, Any?>? = null,
        statusCode: HttpStatus = HttpStatus.OK
    ): ResponseBodyExtractionOptions = RestAssured.given()
        .contentType("application/json")
        .`when`()
        .let { spec ->
            query
                ?.let { spec.get("$path?${it.asQueryString()}") }
                ?: spec.get(path)
        }
        .then()
        .statusCode(statusCode.value())
        .extract()
        .body()

    protected fun makeRequest(
        path: String = "",
        payload: TransactionDTO? = null,
        usePut: Boolean = false,
        statusCode: HttpStatus = HttpStatus.OK
    ): ResponseBodyExtractionOptions = RestAssured.given()
        .contentType("application/json")
        .let { spec -> if (payload == null) spec else spec.body(payload) }
        .`when`()
        .let { spec -> if (usePut) spec.put(path) else spec.post(path) }
        .then()
        .statusCode(statusCode.value())
        .extract()
        .body()

    protected fun generateTransactionDTO(userId: UUID? = null, walletId: UUID? = null, value: Double) =
        TransactionDTO(userId = userId, walletId = walletId, value = value)

    protected fun ResponseBodyExtractionOptions.asString(
        vararg ignoringFields: String
    ): String = asString().let { rawString ->
        try {
            JSONObject(rawString).apply { ignoringFields.forEach { field -> remove(field) } }.toString()
        } catch (_: Exception) {
            rawString
        } catch (_: Error) {
            rawString
        }
    }

}
