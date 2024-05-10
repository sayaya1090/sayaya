package net.sayaya.client.url;

import dagger.Provides;
import elemental2.dom.DomGlobal;
import net.sayaya.client.api.FetchApi;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class ContentUrlChangeTestModule  {
    @Provides @Singleton @Named("contentUrl") static BehaviorSubject<String> provideContentUrl() {
        var content = DomGlobal.document.getElementById("content");
        return behavior(content.getAttribute("src"));
    }
}