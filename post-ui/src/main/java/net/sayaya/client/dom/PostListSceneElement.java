package net.sayaya.client.dom;

import elemental2.dom.MutationRecord;
import elemental2.dom.ShadowRootInit;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import net.sayaya.client.list.PostListScene;

@JsType
public class PostListSceneElement extends CustomElement implements IsFrame {
    @JsIgnore public static void initialize(PostListSceneElement instance, PostListScene scene) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        instance.scene = scene;
        shadowRoot.append(
                instance.scene.element()
        );
    }
    private PostListScene scene;
    @Override public void attach(MutationRecord mutationRecord) {

    }
}
