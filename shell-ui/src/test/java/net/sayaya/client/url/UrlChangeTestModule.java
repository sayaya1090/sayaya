package net.sayaya.client.url;

import dagger.Provides;
import elemental2.dom.DomGlobal;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class UrlChangeTestModule {
    @Provides @Singleton @Named("url") static BehaviorSubject<String> provideUrl() {
        return behavior(null);
    }
}