package net.sayaya.client;

import dagger.Provides;
import net.sayaya.client.api.FetchApi;

@dagger.Module
public class PostModule {
    @Provides FetchApi fetch() { return new FetchApi() {}; }
}
