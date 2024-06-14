package net.sayaya.client;

import dagger.Provides;
import net.sayaya.client.api.FetchApi;
import net.sayaya.client.component.ConsoleElementBuilder;
import net.sayaya.client.component.Logger;
import net.sayaya.client.data.Progress;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Singleton;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class LoginModule {
    @Provides Logger logger(ConsoleElementBuilder consoleElementBuilder) {
        return consoleElementBuilder;
    }
    @Provides FetchApi fetch() { return new FetchApi() {}; }
    @Provides @Singleton BehaviorSubject<Progress> provideProgress() {
        return behavior(null);
    }
}
