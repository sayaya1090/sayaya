package net.sayaya.client.edit;

import dagger.Provides;
import elemental2.core.Global;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import elemental2.promise.Promise;
import net.sayaya.client.api.FetchApi;

import javax.inject.Singleton;

@dagger.Module
public class PostEditTestModule {
    @Provides @Singleton static FetchApi fetchApi() {
        return new FetchApi() {
            @Override public Promise<Response> request(String url, RequestInit param) {
                url = Global.decodeURI(url);
                return Promise.resolve(Response.error());
            }
        };
    }
}

