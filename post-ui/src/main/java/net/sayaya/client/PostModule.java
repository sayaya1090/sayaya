package net.sayaya.client;

import dagger.Provides;
import net.sayaya.client.api.FetchApi;
import net.sayaya.client.data.Progress;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class PostModule {
    @Provides FetchApi fetch() { return new FetchApi() {}; }
    @Provides @Singleton @Named("url") BehaviorSubject<String> provideContentUrl() {
        return behavior("");
    }
    @Provides @Singleton BehaviorSubject<Progress> provideProgress() {
        return behavior(null);
    }
}
