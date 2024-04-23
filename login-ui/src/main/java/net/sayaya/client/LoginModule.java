package net.sayaya.client;

import dagger.Provides;
import net.sayaya.client.api.FetchApi;
import net.sayaya.client.component.Console;
import net.sayaya.client.component.Logger;

@dagger.Module
public class LoginModule {
    @Provides Logger logger(Console console) {
        return console;
    }
    @Provides FetchApi fetch() { return new FetchApi() {}; }
}
