package net.sayaya.client.dom;

import elemental2.dom.DomGlobal;
import elemental2.dom.ShadowRootInit;
import jsinterop.annotations.JsType;
import net.sayaya.client.PostComponent;
import net.sayaya.client.edit.PostEditScene;

@JsType
public class PostEditSceneElement extends CustomElement implements IsFrame {
    public static void initialize(PostEditSceneElement instance, PostComponent components) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        instance.scene = components.postEditScene();
        shadowRoot.append(
                instance.scene.element()
        );
    }
    private PostEditScene scene;
    @Override
    public void draw() {

    }
    @Override
    public void onHashChange(String hash) {
        DomGlobal.console.log("Hash:" + hash);
    }
}
