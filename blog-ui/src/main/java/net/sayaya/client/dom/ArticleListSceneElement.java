package net.sayaya.client.dom;

import elemental2.dom.HTMLLinkElement;
import elemental2.dom.MutationRecord;
import elemental2.dom.ShadowRootInit;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import net.sayaya.client.list.ArticleListScene;

import static org.jboss.elemento.Elements.htmlElement;

@JsType
public class ArticleListSceneElement extends CustomElement implements IsFrame {
    @JsIgnore public static void initialize(ArticleListSceneElement instance, ArticleListScene scene) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        instance.scene = scene;
        shadowRoot.append(
                htmlElement("link", HTMLLinkElement.class).attr("rel", "stylesheet").attr("href", "css/blog.css").element(),
                instance.scene.element()
        );
    }
    private ArticleListScene scene;
    @Override public void attach(MutationRecord mutationRecord) {

    }
}
