package net.sayaya.client.list;

import dagger.Provides;
import net.sayaya.client.api.FetchApi;

@dagger.Module
public class PostListTestModule {
    @Provides
    FetchApi fetch() { return new FetchApi() {}; }
}

