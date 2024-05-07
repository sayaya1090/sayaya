package net.sayaya.client.url;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UrlChangeListener {
    private final FrameUpdater frameUpdater;
    private final HistoryUpdater historyUpdater;
    private final MetaUpdater metaUpdater;
    @Inject UrlChangeListener(FrameUpdater frameUpdater, HistoryUpdater historyUpdater, MetaUpdater metaUpdater) {
        this.frameUpdater = frameUpdater;
        this.historyUpdater = historyUpdater;
        this.metaUpdater = metaUpdater;
    }
    public void listen() {
        frameUpdater.listen();
        historyUpdater.listen();
        metaUpdater.listen();
    }
}
