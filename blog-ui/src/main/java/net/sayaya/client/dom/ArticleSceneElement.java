package net.sayaya.client.dom;

import elemental2.dom.MutationRecord;
import elemental2.dom.ShadowRootInit;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType
public class ArticleSceneElement extends CustomElement implements IsFrame {
    @JsIgnore public static void initialize(ArticleSceneElement instance) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);

    }
    @Override public void attach(MutationRecord mutationRecord) {

    }
}
