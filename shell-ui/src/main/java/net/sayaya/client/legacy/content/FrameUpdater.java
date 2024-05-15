package net.sayaya.client.legacy.content;

import com.google.gwt.core.client.Scheduler;
import elemental2.dom.*;
import net.sayaya.rx.subject.BehaviorSubject;

import java.util.logging.Logger;

import static org.jboss.elemento.Elements.iframe;

public class FrameUpdater {
    private static final Logger logger = Logger.getLogger(FrameUpdater.class.getName());
    private final BehaviorSubject<String> contentUrl;
    private String previous = null;
    private final String prefix = DomGlobal.window.location.protocol + "//" + DomGlobal.window.location.host;
    private HTMLElement frame = (HTMLElement) DomGlobal.document.getElementById("frame");
    private final HTMLElement parent = (HTMLElement) DomGlobal.document.getElementById("content");
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
            if(frame instanceof HTMLIFrameElement) {
                HTMLIFrameElement cast = (HTMLIFrameElement) frame;
                cast.contentWindow.history.replaceState("", DomGlobal.document.title, evt);
                cast.contentWindow.onhashchange.onInvoke(null);
            }
        } else {
            previous = url;
            fadeOut(frame);
            var prev = frame;
            DomGlobal.setTimeout(a -> {
                prev.remove();
                if(evt.contains("-")) {
                    frame = (HTMLElement) DomGlobal.document.createElement(evt);
                    t(frame);
                } else frame = iframe(evt).id("frame").css("content").style("top: 3rem;").attr("allow", "autoplay").element();
                parent.appendChild(frame);
                Scheduler.get().scheduleDeferred(() -> fadeIn(frame));
            }, 80);
        }
    }
    public native void t(Element elem) /*-{
        elem.prepare();
    }-*/;
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