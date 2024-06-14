package net.sayaya.client.url;

import dagger.Provides;
import net.sayaya.client.S;
import net.sayaya.rx.subject.BehaviorSubjectJs;

import javax.inject.Named;
import javax.inject.Singleton;

@dagger.Module
public class UrlChangeTestModule {
    @Provides @Singleton @Named("url") static BehaviorSubjectJs<String> provideUrl() {
        return new S<>(null);
    }
}