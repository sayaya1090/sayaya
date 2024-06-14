package net.sayaya.client.content.component;

import com.google.gwt.core.client.Scheduler;
import elemental2.dom.*;
import net.sayaya.client.content.dom.IsFrame;
import net.sayaya.client.data.Page;
import net.sayaya.client.data.Progress;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.BehaviorSubjectJs;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.createHtmlElement;

@Singleton
public class FrameUpdater {
    private final BehaviorSubject<Page> page;
    private final BehaviorSubjectJs<String> url;
    private final Progress progress;
    private Page previous = null;
    private HTMLElement tag = null;
    @Inject FrameUpdater(BehaviorSubject<Page> page, @Named("url") BehaviorSubjectJs<String> url, Progress progress) {
        this.page = page;
        this.url = url;
        this.progress = progress;
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
        IsFrame.attach(tag);
        IsFrame.url(tag, url);
        IsFrame.progress(tag, progress.enabled(false).max(0.0).value(0.0).intermediate(false));
        update(page.uri);
        Scheduler.get().scheduleDeferred(()->fadeIn(next));
        DomGlobal.setTimeout(a -> {
            old.remove();
            IsFrame.detach(tag);
        }, 100);
    }
    private void update(String url) {
        if(url!=null && url.contains("#")) {
            var hash = url.substring(url.indexOf("#")+1);
            IsFrame.onHashChange(tag, hash);
        }
    }
    private static void fadeOut(HTMLElement elem) {
        elem.classList.add("frame-out");
    }
    private static void fadeIn(HTMLElement elem) {
        elem.classList.remove("frame-in");
    }
}
