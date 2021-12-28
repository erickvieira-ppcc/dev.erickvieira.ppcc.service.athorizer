package dev.erickvieira.ppcc.service.banking.integrated

import dev.erickvieira.ppcc.service.banking.web.api.model.TransactionDTO
import io.restassured.RestAssured
import io.restassured.response.ResponseBodyExtractionOptions
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("test")
open class BankingServiceIntegratedTests {

    protected fun makeRequest(transactionDTO: TransactionDTO) = RestAssured.given()
        .contentType("application/json")
        .body(transactionDTO)
        .`when`()
        .post()
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .body()
        .asString("timestamp")

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
