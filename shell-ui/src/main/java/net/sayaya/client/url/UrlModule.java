package net.sayaya.client.url;

import dagger.Provides;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

@dagger.Module
public class UrlModule {
    @Provides @Singleton static HistoryUpdater provideHistoryUpdater(@Named("url") BehaviorSubject<String> contentUrl) {
        return new HistoryUpdater(contentUrl);
    }
}
