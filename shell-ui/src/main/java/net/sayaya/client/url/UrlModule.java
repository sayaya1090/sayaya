package net.sayaya.client.url;

import dagger.Provides;
import elemental2.dom.DomGlobal;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class UrlModule {
    @Provides @Singleton @Named("contentUrl") static BehaviorSubject<String> provideContentUrl() {
        var content = DomGlobal.document.getElementById("content");
        return behavior(content.getAttribute("src"));
    }
    @Provides @Singleton static FrameUpdater provideFrameUpdater(@Named("contentUrl") BehaviorSubject<String> contentUrl) {
        return new FrameUpdater(contentUrl);
    }
    @Provides @Singleton static HistoryUpdater provideHistoryUpdater(@Named("contentUrl") BehaviorSubject<String> contentUrl) {
        return new HistoryUpdater(contentUrl);
    }
    @Provides @Singleton static MetaUpdater provideMetaUpdater(@Named("contentUrl") BehaviorSubject<String> contentUrl) {
        return new MetaUpdater(contentUrl);
    }
}
