package net.sayaya.client

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import net.sayaya.gwt.test.GwtSpec
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import java.lang.Thread.sleep

class ContentUrlChangeTest: GwtSpec({
    Given(name="iframe src 관리 컴포넌트 테스트", html="contentUrlChange.html", module="net.sayaya.ContentUrlChange",
        js=listOf("js/bundle.js", "js/rxjs.umd.min.js"), timeout = 720
    ) { document ->
        val js = document as JavascriptExecutor
        val history = js.executeScript("return window.history.length") as Long
        document.findElement(By.id("content")).getAttribute("src") shouldBe ""
        When("새로운 페이지 이동이 요청되면(contentUrl 변경)") {
            document.findElement(By.id("test1")).click()
            // history: 1
            Then("부모창의 주소가 변경된다") {
                document.currentUrl shouldEndWith "test1.html"
            }
            Then("새로운 프레임으로 전환(fade in)된다") {
                sleep(100)
                document.findElement(By.id("content")).getAttribute("src") shouldEndWith "test1"
            }
            Then("히스토리가 저장된다") {
                js.executeScript("return window.history.length") shouldBe history+1
            }
        }
        And("페이지를 여러번 이동하면") {
            document.findElement(By.id("test2")).click()
            sleep(50)
            document.findElement(By.id("test3")).click()
            sleep(50)
            document.findElement(By.id("test4")).click()
            // history: 1 -> 2 -> 3 -> 4(*)
            Then("부모창의 주소가 변경된다") {
                document.currentUrl shouldEndWith "test4.html"
            }
            Then("새로운 프레임으로 전환(fade in)된다") {
                sleep(100)
                document.findElement(By.id("content")).getAttribute("src") shouldEndWith "test4"
            }
            Then("히스토리가 저장된다") {
                js.executeScript("return window.history.length") shouldBe history+4
            }
        }
        And("3번 back 하면") {
            document.navigate().back()
            document.navigate().back()
            document.navigate().back()
            // history: 1(*) -> 2 -> 3 -> 4
            Then("부모창의 주소가 변경된다") {
                document.currentUrl shouldEndWith "test1.html"
            }
            Then("히스토리는 변하지 않는다") {
                js.executeScript("return window.history.length") shouldBe history+4
            }
        }
        And("Forward 하면") {
            document.navigate().forward()
            // history: 1 -> 2(*) -> 3 -> 4
            Then("부모창의 주소가 변경된다") {
                document.currentUrl shouldEndWith "test2.html"
            }
            Then("히스토리는 변하지 않는다") {
                js.executeScript("return window.history.length") shouldBe history+4
            }
        }
        And("다시 새로운 페이지 이동이 요청되면(contentUrl 변경)") {
            // history: 1 -> 2 -> 1(*)
            document.findElement(By.id("test1")).click()
            Then("부모창의 주소가 변경된다") {
                document.currentUrl shouldEndWith "test1.html"
            }
            Then("히스토리가 저장된다") {
                js.executeScript("return window.history.length") shouldBe history+3
            }
        }
        And("페이지를 직접 변경한 다음 다시 돌아오면") {
            document.navigate().to(document.currentUrl.replace("test1.html", "contentUrlChange.html"))
            document.navigate().back()
            // history: 1 -> 2 -> 1(*) -> contentUrlChange
            Then("부모창의 주소가 돌아온다") {
                document.currentUrl shouldEndWith "test1.html"
            }
            Then("히스토리는 변하지 않는다") {
                js.executeScript("return window.history.length") shouldBe history+4
            }
        }
    }
})