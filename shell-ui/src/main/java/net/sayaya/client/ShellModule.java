package net.sayaya.client;

import dagger.Provides;
import net.sayaya.client.api.FetchApi;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.BehaviorSubjectJs;

import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class ShellModule {
    @Provides @Singleton static FetchApi provideFetchApi() { return new FetchApi() {}; }
    @Provides @Singleton @Named("url") static BehaviorSubjectJs<String> provideContentUrl() {
        return new S<>("");
    }
}
