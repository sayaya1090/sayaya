package net.sayaya.client;

import dagger.Provides;
import net.sayaya.client.api.FetchApi;
import net.sayaya.client.data.JsWindow;
import net.sayaya.rx.subject.BehaviorSubjectJs;

import javax.inject.Named;
import javax.inject.Singleton;

@dagger.Module
public class ShellModule {
    @Provides @Singleton static FetchApi provideFetchApi() { return new FetchApi() {}; }
    @Provides @Singleton @Named("url") static BehaviorSubjectJs<String> provideContentUrl() {
        JsWindow.url = new BehaviorSubjectJs<>("");
        return JsWindow.url;
    }
}
