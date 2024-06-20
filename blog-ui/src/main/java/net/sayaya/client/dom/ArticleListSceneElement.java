package net.sayaya.client.dom;

import elemental2.dom.MutationRecord;
import elemental2.dom.ShadowRootInit;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType
public class ArticleListSceneElement extends CustomElement implements IsFrame {
    @JsIgnore public static void initialize(ArticleListSceneElement instance) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);

    }
    @Override public void attach(MutationRecord mutationRecord) {

    }
}
