package net.sayaya.client.api;

import elemental2.dom.RequestInit;
import elemental2.promise.Promise;
import net.sayaya.client.data.Post;
import net.sayaya.client.data.PostRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

import static elemental2.core.Global.JSON;

@Singleton
public class PostApi {
    private final FetchApi fetchApi;
    @Inject PostApi(FetchApi fetchApi) {
        this.fetchApi = fetchApi;
    }
    public Promise<String> save(PostRequest post) {
        RequestInit request = RequestInit.create();
        request.setMethod("PUT");
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/json"},
                new String[] {"Accept", "application/vnd.sayaya.v1"}
        });
        request.setBody(JSON.stringify(post));
        return fetchApi.request("/posts", request).then(response->{
            if(response.status==200)        return response.text();
            else if(response.status==204)   return Promise.reject("Empty result");
            return Promise.reject(response.statusText);
        });
    }
}
