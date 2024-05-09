package net.sayaya.client.url;

import com.google.gwt.core.client.Scheduler;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLIFrameElement;
import net.sayaya.rx.subject.BehaviorSubject;

import java.util.logging.Logger;

import static org.jboss.elemento.Elements.iframe;

public class FrameUpdater {
    private static final Logger logger = Logger.getLogger(HistoryUpdater.class.getName());
    private final BehaviorSubject<String> contentUrl;
    private String previous = null;
    private final String prefix = DomGlobal.window.location.protocol + "//" + DomGlobal.window.location.host;
    private HTMLIFrameElement content = (HTMLIFrameElement) DomGlobal.document.getElementById("content");
    private final HTMLElement parent = (HTMLElement) content.parentNode;
    public FrameUpdater(BehaviorSubject<String> contentUrl) {
        this.contentUrl = contentUrl;
        previous = getUrl(contentUrl.getValue());
        if(previous!=null && !previous.startsWith(prefix)) previous = prefix + previous;
    }
    void listen() {
        contentUrl.subscribe(this::update);
    }
    private void update(String evt) {
        if (evt == null) return;
        var url = getUrl(evt);
        if(!url.startsWith(prefix)) url = prefix + url;
        if(url.equals(previous)) {
            content.contentWindow.history.replaceState("", DomGlobal.document.title, evt);
            content.contentWindow.onhashchange.onInvoke(null);
        } else {
            previous = url;
            fadeOut(content);
            var prev = content;
            DomGlobal.setTimeout(a -> {
                prev.remove();
                content = iframe(evt).id("content").css("content").style("top: 3rem;").attr("allow", "autoplay").element();
                parent.appendChild(content);
                Scheduler.get().scheduleDeferred(() -> fadeIn(content));
            }, 80);
        }
    }
    private static String getUrl(String contentUrl) {
        if(contentUrl == null) return null;
        var match = contentUrl.split("#");
        if(match.length == 0) return null;
        return match[0];
    }
    private static void fadeOut(HTMLElement elem) {
        var style = elem.style;
        style.top = "-3rem";
        style.opacity = CSSProperties.OpacityUnionType.of("0");
    }
    private static void fadeIn(HTMLElement elem) {
        var style = elem.style;
        style.top = "0";
        style.opacity = CSSProperties.OpacityUnionType.of("1");
    }
}