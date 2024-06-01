package net.sayaya

import net.sayaya.gwt.test.GwtSpec

class PostListTest: GwtSpec({
    Given(name="콘솔 컴포넌트 테스트", html="postlist.html", module="net.sayaya.PostListTest",
        js=listOf(
            "net.sayaya.Card/net.sayaya.Card.nocache.js",
            "js/bundle.js",
            "js/rxjs.umd.min.js"
        ), css=listOf(
            "css/global.css"),
        timeout = 720
    ) { document ->
        When("테스트1") {
            Then("결과1") {

            }
        }
    }
})