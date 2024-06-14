package net.sayaya

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.string.shouldBeBlank
import io.kotest.matchers.string.shouldNotBeBlank
import net.sayaya.gwt.test.GwtSpec
import org.openqa.selenium.By
import java.lang.Thread.sleep

class ConsoleTest: GwtSpec({
    Given(name="콘솔 컴포넌트 테스트", html="console.html#$MESSAGE", module="net.sayaya.Console",
        js=listOf(
            "js/bundle.js",
            "js/rxjs.umd.min.js"
        ), css=listOf(
            "css/global.css",
            "css/login.css"),
        timeout = 720
    ) { document ->
        val console = document.findElement(By.id("console")).shadowRoot.findElement(By.className("console"))
        val type = document.findElement(By.id("type"))
        val print = document.findElement(By.id("print"))
        val cls = document.findElement(By.id("clear"))
        When("문구 Type 요청 0.2초가 지나면") {
            type.click()
            sleep(200)
            Then("일부 문자가 출력된다") {
                console.text.shouldNotBeBlank()
                console.text shouldNotBeEqual MESSAGE
            }
        }
        sleep(100)
        When("충분한 시간이 지나면") {
            sleep(1000)
            Then("모든 문자가 출력된다") {
                console.text shouldBeEqual MESSAGE
            }
        }
        Then("출력 문자열 초기화") {
            cls.click()
            sleep(10)
            console.text.shouldBeBlank()
        }
        When("문구 Print 요청 직후에") {
            print.click()
            sleep(10)
            Then("즉시 출력이 완료된다") {
                console.text shouldBeEqual MESSAGE
            }
        }
    }
}) {
    companion object {
        const val MESSAGE = "Hello, World!!"
    }
}
