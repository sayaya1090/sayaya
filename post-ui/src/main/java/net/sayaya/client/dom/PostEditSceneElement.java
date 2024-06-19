package net.sayaya.client.dom;

import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLinkElement;
import elemental2.dom.MutationRecord;
import elemental2.dom.ShadowRootInit;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import net.sayaya.client.api.PostApi;
import net.sayaya.client.data.JsWindow;
import net.sayaya.client.data.Post;
import net.sayaya.client.edit.PostEditScene;
import net.sayaya.rx.subject.BehaviorSubject;

import static org.jboss.elemento.Elements.htmlElement;
import static org.jboss.elemento.Elements.script;

@JsType
public class PostEditSceneElement extends CustomElement implements IsFrame {
    @JsIgnore public static void initialize(PostEditSceneElement instance, PostEditScene scene, PostApi api, BehaviorSubject<Post> post) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        instance.scene = scene;
        instance.api = api;
        instance.post = post;
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
    private PostApi api;
    private BehaviorSubject<Post> post;
    @Override
    public void attach(MutationRecord mutationRecord) {
        var url = JsWindow.url.getValue();
        var hash = url.contains("#")? url.substring(url.indexOf("#")+1) : "";
        post.next(new Post().title("").markdown("").html("").images(new net.sayaya.client.data.Image[0]));
        if("new".equalsIgnoreCase(hash)) return;
        api.find(hash).then(p->{
            post.next(p);
            return null;
        });
    }
    private native static void fa(HTMLElement k) /*-{
        $wnd.FontAwesome.dom.watch({
          autoReplaceSvgRoot: k.shadowRoot,
          observeMutationsRoot: k.shadowRoot
        })
    }-*/;
}
