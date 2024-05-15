package net.sayaya.client

import net.sayaya.gwt.test.GwtSpec

class ComponentTest: GwtSpec({
    Given(name="컴포넌트 테스트", html="component.html", module="net.sayaya.Component",
        js=listOf("js/bundle.js", "js/rxjs.umd.min.js", "js/fontawesome.min.js", "js/sharp-light.min.js", "js/sharp-solid.min.js"),
        css=listOf("css/fontawesome.min.css", "css/sharp-light.min.css", "css/sharp-solid.min.css", "css/global.css", "css/index.css"),
        timeout = 720
    ) { document ->
        When("새로운 페이지 이동이 요청되면(contentUrl 변경)") {
            Then("부모창의 주소가 변경된다") {

            }
        }
    }
})