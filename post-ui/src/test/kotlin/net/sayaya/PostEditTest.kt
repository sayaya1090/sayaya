package net.sayaya

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.matchers.string.shouldStartWith
import net.sayaya.client.data.Post
import net.sayaya.gwt.test.GwtSpec
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.logging.LogType
import java.lang.Thread.sleep


@OptIn(ExperimentalKotest::class)
class PostEditTest: GwtSpec({
    Given(name="편집 컴포넌트 테스트", html="postedit.html", module="net.sayaya.PostEditTest",
        js=listOf("net.sayaya.Card/net.sayaya.Card.nocache.js", "js/bundle.js", "js/theme.js", "js/rxjs.umd.min.js", "js/fontawesome.min.js", "js/sharp-light.min.js"),
        css=listOf( "css/fontawesome.min.css", "css/sharp-light.min.css", "css/global.css", "css/post.css"),
        timeout = 720
    ) { document ->
        for(log in document.manage().logs().get(LogType.BROWSER)) println(log.message)
        val act = Actions(document)
        val body = document.findElement(By.tagName("body"))
        val controller = body.findElement(By.tagName("sac-post-edit")).shadowRoot.findElement(By.className("controller"))
        val editor = body.findElement(By.tagName("sac-post-edit")).shadowRoot.findElement(By.className("content-editor"))
        val postJson = document.findElement(By.id("post-json"))
        val om = ObjectMapper()
        When("제목을 수정하면") {
            val titleModified = "Title modified"
            controller.findElement(By.id("title")).sendKeys(
                Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE,
                titleModified,
                Keys.RETURN)
            Then("수정한 제목이 Post에 반영된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).title shouldBeEqual titleModified
            }
        }
        When("본문을 입력하면") {
            val contents = "Hello, World!!"
            editor.findElement(By.id("content")).sendKeys(contents)
            postJson.click() // editor에 Blur 이벤트 발생하여 업데이트 트리거
            Then("입력한 본문이 Post에 반영된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual contents
            }
            Then("Preview에 입력한 내용에 대응되는 HTML이 출력된다") {
                sleep(300) // 디바운스가 걸려 있어 지연시간이 필요하다
                val html = editor.findElement(By.className("html-preview")).getAttribute("innerHTML")
                html.trim() shouldBeEqual "<p>$contents</p>"
            }
        }
        When("내용을 수정하고 Undo 버튼을 누르면") {
            val contents = "Hello, World!!"
            val appendValue = " modified value"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            sleep(200)
            editor.findElement(By.id("content")).sendKeys(appendValue)
            editor.findElement(By.id("undo")).click()
            Then("원래 내용으로 복구된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual contents
            }
            And("Redo 버튼을 누르면") {
                editor.findElement(By.id("redo")).click()
                Then("수정 후의 결과로 돌아온다") {
                    val json = postJson.getAttribute("value")
                    om.readValue(json, Post::class.java).markdown shouldBeEqual contents + appendValue
                }
            }
        }
        When("Header 버튼을 누르면") {
            val contents = "Hello, World!!"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            editor.findElement(By.id("header")).click()
            Then("라인의 가장 앞에 \"# \"이 추가된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual "# $contents"
            }
            Then("기존 라인이 선택되어 있다") {
                val selection = document.selectedText(editor.findElement(By.id("content")))
                selection.shouldNotBeBlank()
                selection!! shouldBeEqual contents
            }
            And("한번 더 Header 버튼을 누르면") {
                editor.findElement(By.id("header")).click()
                Then("라인의 가장 앞에 \"#\"이 추가된다") {
                    val json = postJson.getAttribute("value")
                    om.readValue(json, Post::class.java).markdown shouldBeEqual "## $contents"
                }
            }
        }
        When("입력한 내용 중 일부를 선택하고 Bold 버튼을 누르면") {
            val contents = "Hello, World!!"
            val substring = "World"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            document.selectText(editor.findElement(By.id("content")), contents.indexOf(substring), contents.indexOf(substring)+substring.length)        // World
            editor.findElement(By.id("bold")).click()
            Then("선택한 텍스트 좌우로 \"**\"이 추가된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual contents.replace(substring, "**$substring**")
            }
            Then("선택 영역은 변하지 않는다") {
                val selection = document.selectedText(editor.findElement(By.id("content")))
                selection.shouldNotBeBlank()
                selection!! shouldBeEqual substring
            }
            And("한번 더 Bold 버튼을 누르면") {
                editor.findElement(By.id("bold")).click()
                Then("좌우의 \"**\"가 삭제된다") {
                    val json = postJson.getAttribute("value")
                    om.readValue(json, Post::class.java).markdown shouldBeEqual contents
                }
                Then("선택 영역은 변하지 않는다") {
                    val selection = document.selectedText(editor.findElement(By.id("content")))
                    selection.shouldNotBeBlank()
                    selection!! shouldBeEqual substring
                }
            }
        }
        When("입력한 내용 중 일부를 선택하고 Italic 버튼을 누르면") {
            val contents = "Hello, World!!"
            val substring = "World"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            document.selectText(editor.findElement(By.id("content")), contents.indexOf(substring), contents.indexOf(substring)+substring.length)        // World
            editor.findElement(By.id("italic")).click()
            Then("선택한 텍스트 좌우로 \"_\"이 추가된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual contents.replace(substring, "_${substring}_")
            }
            Then("선택 영역은 변하지 않는다") {
                val selection = document.selectedText(editor.findElement(By.id("content")))
                selection.shouldNotBeBlank()
                selection!! shouldBeEqual substring
            }
            And("한번 더 Italic 버튼을 누르면") {
                editor.findElement(By.id("italic")).click()
                Then("좌우의 \"_\"가 삭제된다") {
                    val json = postJson.getAttribute("value")
                    om.readValue(json, Post::class.java).markdown shouldBeEqual contents
                }
                Then("선택 영역은 변하지 않는다") {
                    val selection = document.selectedText(editor.findElement(By.id("content")))
                    selection.shouldNotBeBlank()
                    selection!! shouldBeEqual substring
                }
            }
        }
        When("입력한 내용 중 일부를 선택하고 Strike 버튼을 누르면") {
            val contents = "Hello, World!!"
            val substring = "World"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            document.selectText(editor.findElement(By.id("content")), contents.indexOf(substring), contents.indexOf(substring)+substring.length)        // World
            editor.findElement(By.id("strike")).click()
            Then("선택한 텍스트 좌우로 \"~~\"이 추가된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual contents.replace(substring, "~~${substring}~~")
            }
            Then("선택 영역은 변하지 않는다") {
                val selection = document.selectedText(editor.findElement(By.id("content")))
                selection.shouldNotBeBlank()
                selection!! shouldBeEqual substring
            }
            And("한번 더 Strike 버튼을 누르면") {
                editor.findElement(By.id("strike")).click()
                Then("좌우의 \"~~\"가 삭제된다") {
                    val json = postJson.getAttribute("value")
                    om.readValue(json, Post::class.java).markdown shouldBeEqual contents
                }
                Then("선택 영역은 변하지 않는다") {
                    val selection = document.selectedText(editor.findElement(By.id("content")))
                    selection.shouldNotBeBlank()
                    selection!! shouldBeEqual substring
                }
            }
        }
        When("Block 버튼을 누르면") {
            val contents = "Hello, World!!"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            editor.findElement(By.id("block")).click()
            Then("라인의 가장 앞에 \"> \"이 추가된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual "> $contents"
            }
            Then("기존 라인이 선택되어 있다") {
                val selection = document.selectedText(editor.findElement(By.id("content")))
                selection.shouldNotBeBlank()
                selection!! shouldBeEqual contents
            }
            And("한번 더 Block 버튼을 누르면") {
                editor.findElement(By.id("block")).click()
                Then("라인의 가장 앞에 \">\"이 추가된다") {
                    val json = postJson.getAttribute("value")
                    om.readValue(json, Post::class.java).markdown shouldBeEqual ">> $contents"
                }
            }
        }
        When("입력한 내용 중 일부를 선택하고 Code 버튼을 누르면") {
            val contents = "Hello, World!!"
            val substring = "World"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            document.selectText(editor.findElement(By.id("content")), contents.indexOf(substring), contents.indexOf(substring)+substring.length)        // World
            editor.findElement(By.id("code")).click()
            Then("선택한 텍스트 좌우로 \"`\"이 추가된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual contents.replace(substring, "`$substring`")
            }
            Then("선택 영역은 변하지 않는다") {
                val selection = document.selectedText(editor.findElement(By.id("content")))
                selection.shouldNotBeBlank()
                selection!! shouldBeEqual substring
            }
            And("한번 더 Code 버튼을 누르면") {
                editor.findElement(By.id("code")).click()
                Then("좌우의 \"`\"가 삭제된다") {
                    val json = postJson.getAttribute("value")
                    om.readValue(json, Post::class.java).markdown shouldBeEqual contents
                }
                Then("선택 영역은 변하지 않는다") {
                    val selection = document.selectedText(editor.findElement(By.id("content")))
                    selection.shouldNotBeBlank()
                    selection!! shouldBeEqual substring
                }
            }
        }
        When("선택 영역 없이 Code 버튼을 누르면") {
            val contents = "Hello, World!!"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            document.selectText(editor.findElement(By.id("content")), contents.indexOf(" "), contents.indexOf(" "))        // World
            editor.findElement(By.id("code")).click()
            Then("커서 위치에서 개행한 다음 \"```\n\n```\n\"이 추가된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual contents.replace(" ", "\n```\n\n```\n ")
            }
            Then("코드 블럭 내부로 커서가 이동한다") {
                val pos = document.selectionStart(editor.findElement(By.id("content")))
                pos shouldNotBe null
                pos!! shouldBeEqual contents.indexOf(" ")+5L // \n```\n
            }
        }
        When("Hr 버튼을 누르면") {
            val contents = "Hello, World!!"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            document.selectText(editor.findElement(By.id("content")), contents.indexOf(" "), contents.indexOf(" "))        // World
            editor.findElement(By.id("hr")).click()
            Then("커서가 위치하는 라인의 끝에서 개행하고 다음 라인에 \"---\n\"이 추가된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual "$contents\n---\n"
            }
            Then("--- 다음 라인으로 커서가 이동한다") {
                val pos = document.selectionStart(editor.findElement(By.id("content")))
                pos shouldNotBe null
                pos!! shouldBeEqual contents.length+5L // \n---\n
            }
        }
        When("입력한 내용 중 일부를 선택하고 Link 버튼을 누르면") {
            val contents = "Hello, World!!"
            val substring = "World"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            document.selectText(editor.findElement(By.id("content")), contents.indexOf(substring), contents.indexOf(substring)+substring.length)        // World
            editor.findElement(By.id("link")).click()
            Then("선택한 텍스트 앞에 '[', 뒤에 '](url)'이 추가된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual contents.replace(substring, "[${substring}](url)")
            }
            Then("선택 영역은 변하지 않는다") {
                val selection = document.selectedText(editor.findElement(By.id("content")))
                selection.shouldNotBeBlank()
                selection!! shouldBeEqual substring
            }
            And("한번 더 Link 버튼을 누르면") {
                editor.findElement(By.id("link")).click()
                Then("추가된 문자열이 삭제된다") {
                    val json = postJson.getAttribute("value")
                    om.readValue(json, Post::class.java).markdown shouldBeEqual contents
                }
                Then("선택 영역은 변하지 않는다") {
                    val selection = document.selectedText(editor.findElement(By.id("content")))
                    selection.shouldNotBeBlank()
                    selection!! shouldBeEqual substring
                }
            }
        }
        When("UL 버튼을 누르면") {
            val contents = "Hello, World!!"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            editor.findElement(By.id("ul")).click()
            Then("라인의 가장 앞에 \"* \"이 추가된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual "* $contents"
            }
            Then("기존 라인이 선택되어 있다") {
                val selection = document.selectedText(editor.findElement(By.id("content")))
                selection.shouldNotBeBlank()
                selection!! shouldBeEqual contents
            }
            And("한번 더 UL 버튼을 누르면") {
                editor.findElement(By.id("ul")).click()
                Then("추가된 \"*\"가 삭제된다") {
                    val json = postJson.getAttribute("value")
                    om.readValue(json, Post::class.java).markdown shouldBeEqual contents
                }
                Then("선택 영역은 변하지 않는다") {
                    val selection = document.selectedText(editor.findElement(By.id("content")))
                    selection.shouldNotBeBlank()
                    selection!! shouldBeEqual contents
                }
            }
        }
        Given("UL 작성 중에") {
            val contents = "    * Hello, World!!"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            var s = 0
            When("리스트 내 임의의 위치에서 엔터 키를 누르면") {
                document.selectText(editor.findElement(By.id("content")), 12, 13)
                editor.findElement(By.id("content")).sendKeys(Keys.ENTER)
                s = document.selectionStart(editor.findElement(By.id("content")))!!.toInt()
                postJson.click()
                val json = postJson.getAttribute("value")
                val md = om.readValue(json, Post::class.java).markdown
                val newline = md.substring(md.indexOf("\n")+1)
                Then("커서 위치 기준으로 개행이 된다") {
                    md shouldStartWith "    * Hello,\n"
                    newline shouldEndWith "World!!"
                }
                Then("새로운 행에는 리스트 태그가 붙어있다") {
                    newline.trim() shouldStartWith "*"
                }
                Then("뎁스는 유지된다") {
                    newline shouldStartWith "    "
                }
            }
            And("탭 키를 누르면") {
                document.selectText(editor.findElement(By.id("content")), s, s+1)
                editor.findElement(By.id("content")).sendKeys(Keys.TAB)
                postJson.click()
                val json = postJson.getAttribute("value")
                val md = om.readValue(json, Post::class.java).markdown
                val newline = md.substring(md.indexOf("\n")+1)
                Then("리스트의 뎁스가 1단계 증가한다") {
                    newline shouldStartWith "        *"
                }
            }
        }
        When("OL 버튼을 누르면") {
            val contents = "Hello, World!!"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            editor.findElement(By.id("ol")).click()
            Then("라인의 가장 앞에 \"1. \"이 추가된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual "1. $contents"
            }
            Then("기존 라인이 선택되어 있다") {
                val selection = document.selectedText(editor.findElement(By.id("content")))
                selection.shouldNotBeBlank()
                selection!! shouldBeEqual contents
            }
            And("한번 더 OL 버튼을 누르면") {
                editor.findElement(By.id("ol")).click()
                Then("추가된 \"1. \"가 삭제된다") {
                    val json = postJson.getAttribute("value")
                    om.readValue(json, Post::class.java).markdown shouldBeEqual contents
                }
                Then("선택 영역은 변하지 않는다") {
                    val selection = document.selectedText(editor.findElement(By.id("content")))
                    selection.shouldNotBeBlank()
                    selection!! shouldBeEqual contents
                }
            }
        }
        Given("OL 작성 중에") {
            val contents = "    9. Hello, World!!\n    10. Hello, World!!"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            var s = 0
            When("리스트 내 임의의 위치에서 엔터 키를 누르면") {
                document.selectText(editor.findElement(By.id("content")), 13, 14)
                editor.findElement(By.id("content")).sendKeys(Keys.ENTER)
                s = document.selectionStart(editor.findElement(By.id("content")))!!.toInt()
                postJson.click()
                val json = postJson.getAttribute("value")
                val md = om.readValue(json, Post::class.java).markdown
                val lines = md.split("\n")
                Then("라인 수가 1 증가한다") { lines.size shouldBeEqual 3 }
                Then("커서 위치 기준으로 개행이 된다") {
                    md shouldStartWith "    9. Hello,\n"
                    lines[1] shouldEndWith "World!!"
                }
                Then("새로운 행에는 리스트 태그가 붙어있다") { lines[1].trim() shouldStartWith "10. " }
                Then("뎁스는 유지된다") { lines[1] shouldStartWith "    " }
                Then("다음 목록의 순번이 조정된다") { lines[2].trim() shouldStartWith "11. " }
            }
            And("탭 키를 누르면") {
                document.selectText(editor.findElement(By.id("content")), s, s+1)
                editor.findElement(By.id("content")).sendKeys(Keys.TAB)
                postJson.click()
                val json = postJson.getAttribute("value")
                val md = om.readValue(json, Post::class.java).markdown
                val lines = md.split("\n")
                Then("라인 수는 변경되지 않는다") { lines.size shouldBeEqual 3 }
                Then("리스트의 뎁스가 1단계 증가한다") {
                    lines[1] shouldStartWith "        "
                }
                Then("순번은 1로 변경된다") { lines[1].trim() shouldStartWith "1. " }
                Then("기존 뎁스 다음 목록의 순번이 조정된다") { lines[2].trim() shouldStartWith "10. " }
            }
        }
        When("Check 버튼을 누르면") {
            val contents = "Hello, World!!"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            editor.findElement(By.id("check")).click()
            Then("라인의 가장 앞에 \"- [ ] \"이 추가된다") {
                val json = postJson.getAttribute("value")
                om.readValue(json, Post::class.java).markdown shouldBeEqual "- [ ] $contents"
            }
            Then("기존 라인이 선택되어 있다") {
                val selection = document.selectedText(editor.findElement(By.id("content")))
                selection.shouldNotBeBlank()
                selection!! shouldBeEqual contents
            }
            And("한번 더 Check 버튼을 누르면") {
                editor.findElement(By.id("check")).click()
                Then("추가된 \"- [ ] \"가 삭제된다") {
                    val json = postJson.getAttribute("value")
                    om.readValue(json, Post::class.java).markdown shouldBeEqual contents
                }
                Then("선택 영역은 변하지 않는다") {
                    val selection = document.selectedText(editor.findElement(By.id("content")))
                    selection.shouldNotBeBlank()
                    selection!! shouldBeEqual contents
                }
            }
        }
        Given("체크리스트 작성 중에") {
            val contents = "    - [ ] Hello, World!!"
            editor.findElement(By.id("content")).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE, contents)
            var s = 0
            When("리스트 내 임의의 위치에서 엔터 키를 누르면") {
                document.selectText(editor.findElement(By.id("content")), 16, 17)
                editor.findElement(By.id("content")).sendKeys(Keys.ENTER)
                s = document.selectionStart(editor.findElement(By.id("content")))!!.toInt()
                postJson.click()
                val json = postJson.getAttribute("value")
                val md = om.readValue(json, Post::class.java).markdown
                val newline = md.substring(md.indexOf("\n")+1)
                Then("커서 위치 기준으로 개행이 된다") {
                    md shouldStartWith "    - [ ] Hello,\n"
                    newline shouldEndWith "World!!"
                }
                Then("새로운 행에는 체크 태그가 붙어있다") {
                    newline.trim() shouldStartWith "- [ ] "
                }
                Then("뎁스는 유지된다") {
                    newline shouldStartWith "    "
                }
            }
            And("탭 키를 누르면") {
                document.selectText(editor.findElement(By.id("content")), s, s+1)
                editor.findElement(By.id("content")).sendKeys(Keys.TAB)
                postJson.click()
                val json = postJson.getAttribute("value")
                val md = om.readValue(json, Post::class.java).markdown
                val newline = md.substring(md.indexOf("\n")+1)
                Then("체크리스트의 뎁스가 1단계 증가한다") {
                    newline shouldStartWith "        - [ ]"
                }
            }
        }
        When("Github Repo 설정이 주어지면") {
            body.findElement(By.id("setup-github-repo-config")).click()
            Then("주어진 Repo 값이 출력된다") {
                controller.findElement(By.id("github-settings")).text shouldContain "user/Repo1:branch"
                controller.findElement(By.id("github-settings")).getCssValue("color").color() shouldBeEqual document.getCssVariable("--md-sys-color-safe").color()
            }
        }
        When("Github Repo 설정이 없으면") {
            body.findElement(By.id("clear-github-repo-config")).click()
            Then("Repo 설정이 안 되어 있다는 정보가 출력된다") {
                controller.findElement(By.id("github-settings")).text shouldContain "No repository assigned"
                controller.findElement(By.id("github-settings")).getCssValue("color").color() shouldBeEqual document.getCssVariable("--md-sys-color-error").color()
            }
        }
    }
}) {
    companion object {
        private const val SELECT_TEXT = "arguments[0].setSelectionRange(arguments[1], arguments[2]);"
        private fun ChromeDriver.selectText(elem: WebElement, start: Int, end: Int) =  (this as JavascriptExecutor).executeScript(SELECT_TEXT, elem, start, end)
        private const val SELECTED_TEXT = "return arguments[0].value.substring(arguments[0].selectionStart, arguments[0].selectionEnd);"
        private fun ChromeDriver.selectedText(elem: WebElement): String? =  (this as JavascriptExecutor).executeScript(SELECTED_TEXT, elem) as String?
        private const val SELECTION_START = "return arguments[0].selectionStart;"
        private fun ChromeDriver.selectionStart(elem: WebElement): Long? =  (this as JavascriptExecutor).executeScript(SELECTION_START, elem) as Long?
        private const val GET_CSS_VARIABLE_SCRIPT = "return getComputedStyle(document.documentElement).getPropertyValue(arguments[0]).trim();"
        private fun ChromeDriver.getCssVariable(variable: String): String = (this as JavascriptExecutor).executeScript(GET_CSS_VARIABLE_SCRIPT, variable) as String
        private data class Color(val red: Int, val green: Int, val blue: Int, val alpha: Float)
        private fun String.color(): Color = when {
            startsWith("rgba") -> fromRgba(this)
            startsWith("rgb") -> fromRgb(this)
            startsWith("#") -> fromHex(this)
            else -> throw IllegalArgumentException("Invalid color format")
        }
        private fun fromRgba(rgba: String): Color {
            val rgbaValues = rgba.replace("rgba(", "").replace(")", "").split(",", " ").map(String::trim).filter(String::isNotEmpty)
            val r = rgbaValues[0].trim().toInt()
            val g = rgbaValues[1].trim().toInt()
            val b = rgbaValues[2].trim().toInt()
            val a = rgbaValues[3].trim().toFloat()
            return Color(r, g, b, a)
        }
        private fun fromRgb(rgb: String): Color {
            val rgbValues = rgb.replace("rgb(", "").replace(")", "").split(",", " ").map(String::trim).filter(String::isNotEmpty)
            val r = rgbValues[0].trim().toInt()
            val g = rgbValues[1].trim().toInt()
            val b = rgbValues[2].trim().toInt()
            return Color(r, g, b, 1.0f)
        }
        private fun fromHex(hex: String): Color {
            val cleanedHex = hex.removePrefix("#")
            val (r, g, b) = when (cleanedHex.length) {
                6 -> { // #RRGGBB
                    val r = cleanedHex.substring(0, 2).toInt(16)
                    val g = cleanedHex.substring(2, 4).toInt(16)
                    val b = cleanedHex.substring(4, 6).toInt(16)
                    Triple(r, g, b)
                }
                3 -> { // #RGB
                    val r = cleanedHex.substring(0, 1).repeat(2).toInt(16)
                    val g = cleanedHex.substring(1, 2).repeat(2).toInt(16)
                    val b = cleanedHex.substring(2, 3).repeat(2).toInt(16)
                    Triple(r, g, b)
                }
                else -> throw IllegalArgumentException("Invalid hex color format")
            }
            return Color(r, g, b, 1.0f)
        }
    }
}