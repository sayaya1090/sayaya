package net.sayaya.client.url;

import net.sayaya.rx.subject.BehaviorSubject;

import java.util.logging.Logger;

public class MetaUpdater {
    private static final Logger logger = Logger.getLogger(MetaUpdater.class.getName());
    private final BehaviorSubject<String> contentUrl;
    public MetaUpdater(BehaviorSubject<String> contentUrl) {
       this.contentUrl = contentUrl;
    }
    void listen() {
        contentUrl.subscribe(this::update);
    }
    private void update(String url) {
       // logger.info("Update meta data for " + url);
    }
}
