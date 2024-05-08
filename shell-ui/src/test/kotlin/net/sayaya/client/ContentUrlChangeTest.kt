package net.sayaya.client

import io.kotest.matchers.shouldBe
import net.sayaya.gwt.test.GwtSpec
import org.openqa.selenium.By

class ContentUrlChangeTest: GwtSpec({
    Given(name="iframe src 관리 컴포넌트 테스트", html="contentUrlChange.html", module="net.sayaya.ContentUrlChange",
        js=listOf("js/bundle.js", "js/rxjs.umd.min.js"), timeout = 720
    ) { document ->
        val child = document.findElement(By.id("content"))
        val change = document.findElement(By.id("change"))
        val clear = document.findElement(By.id("clear"))
        When("앵커 태그 없이 열리면") {
            Then("프레임 URL은 비어 있다") {
                child.getAttribute("src") shouldBe ""
            }
        }
        When("iframe의 URL이 변경되면") {
            change.click()
            Then("프레임 URL이 채워진다") {
                child.getAttribute("src") shouldBe "test.html"
            }
        }
    }
})