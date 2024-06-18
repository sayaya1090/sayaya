package net.sayaya.client;

import dagger.Provides;
import net.sayaya.client.api.FetchApi;
import net.sayaya.client.component.ConsoleElementBuilder;
import net.sayaya.client.component.Logger;

@dagger.Module
public class LoginModule {
    @Provides Logger logger(ConsoleElementBuilder consoleElementBuilder) {
        return consoleElementBuilder;
    }
    @Provides FetchApi fetch() { return new FetchApi() {}; }
}
