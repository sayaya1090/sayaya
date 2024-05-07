package net.sayaya.client.url;

import elemental2.core.JsRegExp;
import elemental2.dom.DomGlobal;
import net.sayaya.rx.subject.BehaviorSubject;

import java.util.logging.Logger;

public class HistoryUpdater {
    private static final Logger logger = Logger.getLogger(HistoryUpdater.class.getName());
    private final JsRegExp URL_EXP = new JsRegExp("([^#]+)(#.+)?$");
    private final BehaviorSubject<String> contentUrl;
    public HistoryUpdater(BehaviorSubject<String> contentUrl) {
       this.contentUrl = contentUrl;
    }
    void listen() {
        contentUrl.subscribe(this::update);
    }
    private void update(String url) {
        if(url == null) return;
        var match = URL_EXP.exec(url);
        var path = match.getAt(1);
        var hash = match.getAt(2);
        var html = path + ".html";
        if(hash!=null && hash.length() > 1) html += hash;
        if(!DomGlobal.window.location.href.equals(html)) DomGlobal.window.history.pushState("", DomGlobal.document.title, html);
    }
}
