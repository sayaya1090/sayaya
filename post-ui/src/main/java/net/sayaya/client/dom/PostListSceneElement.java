package net.sayaya.client.dom;

import elemental2.dom.ShadowRootInit;
import jsinterop.annotations.JsType;
import net.sayaya.client.DaggerPostComponent;
import net.sayaya.client.list.PostListScene;

@JsType
public class PostListSceneElement extends CustomElement implements Drawable {
    public static void initialize(PostListSceneElement instance) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        instance.scene = DaggerPostComponent.create().postListScene();
        shadowRoot.append(
                instance.scene.element()
        );
    }
    private PostListScene scene;
    @Override
    public void prepare(String param) {

    }

    @Override
    public void draw() {

    }
}
