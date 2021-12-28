package dev.erickvieira.ppcc.service.banking.integrated.undo

import com.github.database.rider.core.api.configuration.DBUnit
import com.github.database.rider.core.api.configuration.Orthography
import com.github.database.rider.core.api.dataset.CompareOperation
import com.github.database.rider.core.api.dataset.DataSet
import com.github.database.rider.core.api.dataset.ExpectedDataSet
import com.github.database.rider.junit5.api.DBRider
import dev.erickvieira.ppcc.service.banking.integrated.BankingServiceIntegratedTests
import dev.erickvieira.ppcc.service.banking.integrated.PostgresContainerSetup
import io.restassured.RestAssured
import org.approvaltests.Approvals
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.jdbc.JdbcTestUtils
import java.util.*

@DBRider
@ActiveProfiles("test")
@TestPropertySource(value = ["classpath:application-test.properties"])
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE, caseSensitiveTableNames = true, cacheConnection = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [PostgresContainerSetup::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class UndoPutIT : BankingServiceIntegratedTests() {

    @LocalServerPort
    private val port: Int = 0

    @Autowired
    private val jdbcTemplate: JdbcTemplate? = null

    @BeforeAll
    fun setUp() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/api/v1/banking/undo"
    }

    @AfterEach
    fun tearDown() {
        jdbcTemplate?.apply {
            JdbcTestUtils.deleteFromTables(this, "tb_transaction", "tb_wallet")
        }
    }

    @Test
    @DataSet("undo/initial-state.yml")
    fun `undo - must refund the given payment transaction`() {
        val responseBody = makeRequest(
            path = "/cb27dbd6-18e7-5a4d-af44-013b6d7f029f",
            usePut = true,
        ).asString("timestamp", "id")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("undo/initial-state.yml")
    @ExpectedDataSet(
        "undo/initial-state.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `undo - must decline with error declined_by_transaction_not_found`() {
        val responseBody = makeRequest(
            path = "/4ef996c1-d1e2-530f-b142-75f6ce970c58",
            usePut = true,
            statusCode = HttpStatus.NOT_FOUND
        ).asString("timestamp", "id", "message")
        Approvals.verifyJson(responseBody)
    }

    @Test
    @DataSet("undo/initial-state.yml")
    @ExpectedDataSet(
        "undo/initial-state.yml",
        compareOperation = CompareOperation.CONTAINS
    )
    fun `undo - must decline with error declined_by_prohibed_undo`() {
        val responseBody = makeRequest(
            path = "/c145d561-a810-530d-883d-fb8a400fc87b",
            usePut = true,
            statusCode = HttpStatus.BAD_REQUEST
        ).asString("timestamp", "id", "message")
        Approvals.verifyJson(responseBody)
    }

}