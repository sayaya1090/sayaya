package net.sayaya.login

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.sayaya.JsonConfig
import net.sayaya.R2dbcConfig
import net.sayaya.login.testcontainers.Database
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import java.util.*

@DataR2dbcTest
@Import(R2dbcConfig::class, JsonConfig::class)
@Testcontainers
internal class UserRepositoryTest(private val repo: UserRepository): BehaviorSpec({
    val now = LocalDateTime.now()
    Given("DB 초기화 및 연결 완료"){
        repo shouldNotBe null
        When("존재하지 않는 사용자를 조회하면") {
            val user = repo.findById(UUID.randomUUID()).block()
            Then("null이 반환된다") {
                user shouldBe null
            }
        }
        When("새로운 사용자를 저장하면") {
            val entity = User(id=UUID.randomUUID()).apply { alias="alias" }
            val saved = repo.save(entity).block()
            Then("생성시각, 변경시각이 업데이트 되어 저장된 객체가 반환된다") {
                saved shouldNotBe null
                saved!!.id shouldBeEqual entity.id
                saved.alias!! shouldBeEqual entity.alias!!
                saved.createDateTime shouldBeGreaterThanOrEqualTo now
                saved.lastModifyDateTime shouldBeGreaterThanOrEqualTo now
            }
            And("저장한 사용자를 조회하면") {
                val read = repo.findById(entity.id).block()
                Then("동일한 객체가 반환된다") {
                    read shouldNotBe null
                    read!!.id shouldBeEqual entity.id
                    read.alias!! shouldBeEqual entity.alias!!
                    read.createDateTime shouldBeGreaterThanOrEqualTo now
                    read.lastModifyDateTime shouldBeGreaterThanOrEqualTo now
                }
            }
        }
    }
    Given("특정 ID의 사용자가 존재하고") {
        repo shouldNotBe null
        val id = UUID.fromString("a8e8a479-03b4-492c-ba73-d4bf7e0892d7")
        When("DB에 존재하는 사용자를 요청하면") {
            val user = repo.findById(id).block()
            Then("User 객체를 반환한다.") {
                user shouldNotBe null
                user!!.id shouldBe id
            }
        }
        When("ID가 동일한 사용자를 신규로 저장 시도하면") {
            val entity = User(id=id)
            val request = repo.save(entity)
            Then("DataIntegrityViolationException 예외가 발생한다") {
                shouldThrow<DataIntegrityViolationException> {
                    request.block()
                }
            }
        }
        When("ID가 동일한 사용자를 DB에서 읽어온 다음 변경, 저장하면") {
            val entity = repo.findById(id).block()!!
            val saved = entity.apply { alias="newAlias" }.let(repo::save).block()
            Then("생성시각은 변경되지 않고 변경시각은 업데이트 되어 저장된 객체가 반환된다") {
                saved shouldNotBe null
                saved!!.id shouldBeEqual entity.id
                saved.alias!! shouldBeEqual entity.alias!!
                saved.createDateTime shouldBeLessThanOrEqualTo now
                saved.lastModifyDateTime shouldBeGreaterThanOrEqualTo now
            }
            And("저장한 사용자를 조회하면") {
                val read = repo.findById(entity.id).block()
                Then("변경된 결과가 반환된다") {
                    read shouldNotBe null
                    read!!.id shouldBeEqual entity.id
                    read.alias!! shouldBeEqual entity.alias!!
                    read.createDateTime shouldBeLessThanOrEqualTo now
                    read.lastModifyDateTime shouldBeGreaterThanOrEqualTo now
                }
            }
        }
    }
}) {
    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            Database.registerDynamicProperties(registry)
        }
    }
}