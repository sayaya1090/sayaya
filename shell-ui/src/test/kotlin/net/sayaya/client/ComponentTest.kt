package net.sayaya.client

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.sayaya.client.content.ContentTestModule
import net.sayaya.gwt.test.GwtSpec
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import java.lang.Thread.sleep

class ComponentTest: GwtSpec({
    Given(name="컴포넌트 테스트", html="component.html", module="net.sayaya.Component",
        js=listOf("js/bundle.js", "js/rxjs.umd.min.js", "js/fontawesome.min.js", "js/sharp-light.min.js", "js/sharp-solid.min.js"),
        css=listOf("css/fontawesome.min.css", "css/sharp-light.min.css", "css/sharp-solid.min.css", "css/global.css", "css/index.css"),
        timeout = 840
    ) { document ->
        val actions = Actions(document)
        val btnMenu = document.findElement(By.tagName("sac-menu-button"))
        val rails = document.findElements(By.className("rail"))
        val nav = document.findElement(By.tagName("nav"))

        nav.getAttribute("open") shouldBe null
        When("메뉴 버튼을 누르면") {
            btnMenu.click()
            Then("nav 상태가 open으로 변경된다") {
                nav.getAttribute("open") shouldBe "true"
            }
            Then("Menu Rail이 열리고 Page Menu는 닫혀 있다") {
                rails[0].getAttribute("expand") shouldBe "true"
                rails[1].getAttribute("hide") shouldBe "true"
            }
            Then("메뉴 항목이 보인다") {
                rails[0].findElements(By.tagName("sac-navigation-rail-item")).size shouldBeEqual ContentTestModule.menu.size
            }
            Then("Bottom 메뉴는 Bottom끼리 모여서 하단 정렬되어 있다") {
                rails[0].findElements(By.tagName("sac-navigation-rail-item")).count { i -> i.getCssValue("margin-top") != "0px" } shouldBe 1
                val bottoms = ContentTestModule.menu.filter { m->m.bottom==true }.map { m->m.title }.toSet()
                rails[0].findElements(By.tagName("sac-navigation-rail-item")).dropWhile { it.getCssValue("margin-top") == "0px"  }.count() shouldBeEqual bottoms.size
            }
            And("메뉴 버튼을 다시 누르면") {
                btnMenu.click()
                Then("nav 상태가 closed으로 변경된다") {
                    nav.getAttribute("open") shouldBe null
                }
            }
        }
        When("메뉴를 열어서 Page가 0개 또는 1개인 첫번째 메뉴에 마우스 호버하면") {
            ContentTestModule.menu[0].children.size shouldBeLessThanOrEqual 1
            btnMenu.click()
            val menu1st = rails[0].findElements(By.tagName("sac-navigation-rail-item"))[0]
            actions.moveToElement(menu1st).perform()
            Then("Page Menu는 열리지 않는다") {
                rails[1].getAttribute("hide") shouldBe "true"
            }
            And("첫번째 메뉴를 클릭하면") {
                menu1st.click()
                Then("Menu Rail이 접힌다") {
                    rails[0].getAttribute("collapse") shouldBe "true"
                    rails[1].getAttribute("hide") shouldBe "true"
                }
                Then("nav 상태가 close 로 변경된다") {
                    nav.getAttribute("open") shouldBe null
                }
                Then("지정된 태그가 1초 안에 프레임에 출력된다") {
                    sleep(1000)
                    document.findElement(By.tagName(ContentTestModule.menu[0].children[0].tag)) shouldNotBe null
                }
                And("Menu Rail에서 두번째 메뉴를 클릭하면") {
                    rails[0].findElements(By.tagName("sac-navigation-rail-item"))[1].click()
                    Then("Page Menu Rail로 전환된다") {
                        rails[0].getAttribute("hide") shouldBe "true"
                        rails[1].getAttribute("collapse") shouldBe "true"
                    }
                    Then("지정된 태그가 1초 안에 프레임에 출력된다") {
                        sleep(1000)
                        document.findElement(By.tagName(ContentTestModule.menu[1].children[0].tag)) shouldNotBe null
                    }
                }
            }
        }
        When("메뉴를 열어서 Page가 2개 이상인 두번째 메뉴에 마우스 호버하면") {
            ContentTestModule.menu[1].children.size shouldBeGreaterThan 1
            val scene = document.findElement(By.className("frame")).text
            btnMenu.click()
            val menu2nd = rails[0].findElements(By.tagName("sac-navigation-rail-item"))[1]
            actions.moveToElement(menu2nd).perform()
            Then("Page Menu가 열린다") {
                rails[1].getAttribute("expand") shouldBe "true"
            }
            val pages = rails[1].findElements(By.tagName("sac-navigation-rail-item"))
            Then("Page Menu의 아이템은 부모 아이템과 높이가 정렬되어 있다") {
                menu2nd.location.y.shouldBeBetween(pages[0].location.y-1, pages[0].location.y+1)    // Border 때문에 1px정도는 차이가 날 수 있다
            }
            Then("아직 프레임은 변경되지 않는다") {
                document.findElement(By.className("frame")).text shouldBeEqual scene
            }
            And("첫번째 페이지를 클릭하면") {
                rails[1].findElements(By.tagName("sac-navigation-rail-item"))[0].click()
                Then("Page Rail이 접힌다") {
                    rails[0].getAttribute("hide") shouldBe "true"
                    rails[1].getAttribute("collapse") shouldBe "true"
                }
                Then("nav 상태가 close 로 변경된다") {
                    nav.getAttribute("open") shouldBe null
                }
                Then("지정된 태그가 1초 안에 프레임에 출력된다") {
                    sleep(1000)
                    document.findElement(By.tagName(ContentTestModule.menu[1].children[0].tag)) shouldNotBe null
                }
                And("Page Rail에서 두번째 메뉴를 클릭하면") {
                    rails[1].findElements(By.tagName("sac-navigation-rail-item"))[1].click()
                    Then("Page Menu Rail로 전환된다") {
                        rails[0].getAttribute("hide") shouldBe "true"
                        rails[1].getAttribute("collapse") shouldBe "true"
                    }
                    Then("지정된 태그가 1초 안에 프레임에 출력된다") {
                        sleep(1000)
                        document.findElement(By.tagName(ContentTestModule.menu[1].children[1].tag)) shouldNotBe null
                    }
                }
                And("Page Rail 가장 마지막에 있는 돌아가기 버튼을 클릭하면") {
                    val scene2 = document.findElement(By.className("frame")).text
                    val children = rails[1].findElements(By.tagName("sac-navigation-rail-item"))
                    children[children.size-1].click()
                    Then("Page Rail이 접히고 Menu Rail로 전환된다") {
                        rails[0].getAttribute("collapse") shouldBe "true"
                        rails[1].getAttribute("hide") shouldBe "true"
                    }
                    Then("프레임은 변경되지 않는다") {
                        document.findElement(By.className("frame")).text shouldBeEqual scene2
                    }
                }
            }
        }
    }
})