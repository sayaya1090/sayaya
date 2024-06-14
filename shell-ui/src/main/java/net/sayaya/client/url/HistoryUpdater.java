package net.sayaya.client.url;

import elemental2.dom.DomGlobal;
import net.sayaya.rx.subject.BehaviorSubjectJs;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.logging.Logger;

@Singleton
public class HistoryUpdater {
    private static final Logger logger = Logger.getLogger(HistoryUpdater.class.getName());
    private final BehaviorSubjectJs<String> url;
    @Inject HistoryUpdater(@Named("url") BehaviorSubjectJs<String> url) {
       this.url = url;
    }
    void listen() {
        url.subscribe(this::update);
    }
    private void update(String url) {
        if(url == null) return;
        if(!DomGlobal.window.location.href.equals(url)) {
            logger.info("History.pushState(" + url + ")");
            DomGlobal.window.history.pushState("", DomGlobal.document.title, url);
        }
    }
}
