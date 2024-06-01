package net.sayaya.client.dom;

import com.google.gwt.core.client.Scheduler;
import elemental2.dom.*;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import net.sayaya.client.edit.PostEditScene;
import net.sayaya.client.util.Marked;

import static org.jboss.elemento.Elements.htmlElement;
import static org.jboss.elemento.Elements.script;

@JsType
public class PostEditSceneElement extends CustomElement implements IsFrame {
    @JsIgnore public static void initialize(PostEditSceneElement instance, PostEditScene scene) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        instance.scene = scene;
        shadowRoot.append(
                htmlElement("link", HTMLLinkElement.class).attr("rel", "stylesheet").attr("href", "css/post.css").element(),
                htmlElement("link", HTMLLinkElement.class).attr("rel", "stylesheet").attr("href", "https://cdnjs.cloudflare.com/ajax/libs/github-markdown-css/5.5.0/github-markdown.min.css").element(),
                htmlElement("link", HTMLLinkElement.class).attr("rel", "stylesheet").attr("href", "https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@11.9.0/build/styles/atom-one-dark.min.css").element(),
                script().attr("src", "https://cdn.jsdelivr.net/npm/marked/lib/marked.umd.js").element(),
                script().attr("src", "https://cdn.jsdelivr.net/npm/marked-highlight/lib/index.umd.js").element(),
                script().attr("src", "https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@11.9.0/build/highlight.min.js").element(),
                instance.scene.element()
        );
        fa(instance);
    }
    private PostEditScene scene;
    @Override
    public void attach(MutationRecord mutationRecord) {

    }
    @Override
    public void onHashChange(String hash) {
        DomGlobal.console.log("Hash:" + hash);
    }
    private native static void fa(HTMLElement k) /*-{
        $wnd.FontAwesome.dom.watch({
          autoReplaceSvgRoot: k.shadowRoot,
          observeMutationsRoot: k.shadowRoot
        })
    }-*/;
}
