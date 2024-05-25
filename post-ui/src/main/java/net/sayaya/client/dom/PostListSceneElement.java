package net.sayaya.client.dom;

import elemental2.dom.ShadowRootInit;
import jsinterop.annotations.JsType;
import net.sayaya.client.PostComponent;
import net.sayaya.client.list.PostListScene;

@JsType
public class PostListSceneElement extends CustomElement implements IsFrame {
    public static void initialize(PostListSceneElement instance, PostComponent components) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        instance.scene = components.postListScene();
        shadowRoot.append(
                instance.scene.element()
        );
    }
    private PostListScene scene;
    @Override
    public void draw() {

    }
    @Override
    public void onHashChange(String hash) {

    }
}
