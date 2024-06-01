package net.sayaya.client.edit;

import dagger.Provides;
import net.sayaya.client.api.FetchApi;

@dagger.Module
public class PostEditTestModule {
    @Provides FetchApi fetch() { return new FetchApi() {}; }
}

