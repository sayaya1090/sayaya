package net.sayaya.client.content.component;

import com.google.gwt.core.client.Scheduler;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import net.sayaya.client.data.Page;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.createHtmlElement;

@Singleton
public class FrameUpdater {
    private final BehaviorSubject<Page> page;
    private final BehaviorSubject<String> url;
    private Page previous = null;
    private HTMLElement tag = null;
    @Inject FrameUpdater(BehaviorSubject<Page> page, @Named("url") BehaviorSubject<String> url) {
        this.page = page;
        this.url = url;
        previous = page.getValue();
    }
    public void listen() {
        page.subscribe(this::update);
        url.subscribe(this::update);
    }
    private void update(Page page) {
        if(page==null || page.equals(previous)) return;
        var old = (HTMLElement) DomGlobal.document.getElementsByClassName("frame").item(0);
        HTMLElement parent = (HTMLElement) old.parentNode;
        previous = page;
        fadeOut(old);

        var next = new FrameElementBuilder().css("frame-in").element();
        tag = createHtmlElement(page.tag, HTMLElement.class);
        next.appendChild(tag);
        parent.appendChild(next);
        draw(tag);
        update(page.uri);
        Scheduler.get().scheduleDeferred(()->fadeIn(next));
        DomGlobal.setTimeout(a -> old.remove(), 100);
    }
    private void update(String url) {
        if(url!=null && url.contains("#")) {
            var hash = url.substring(url.indexOf("#")+1);
            onHashChange(tag, hash);
        }
    }
    public static native void draw(Element elem) /*-{
        try { elem.draw(); } catch(e) {}
    }-*/;
    public static native void onHashChange(Element elem, String hash) /*-{
        try { elem.onHashChange(hash); } catch(e) {}
    }-*/;
    private static void fadeOut(HTMLElement elem) {
        elem.classList.add("frame-out");
    }
    private static void fadeIn(HTMLElement elem) {
        elem.classList.remove("frame-in");
    }
}
