package net.sayaya.client.api;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GithubApi {
    private final FetchApi fetchApi;
    @Inject GithubApi(FetchApi fetchApi) {
        this.fetchApi = fetchApi;
    }
}
