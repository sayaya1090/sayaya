package net.sayaya.client.url;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UrlChangeListener {
    private final HistoryUpdater historyUpdater;
    @Inject UrlChangeListener(HistoryUpdater historyUpdater) {
        this.historyUpdater = historyUpdater;
    }
    public void listen() {
        historyUpdater.listen();
    }
}
