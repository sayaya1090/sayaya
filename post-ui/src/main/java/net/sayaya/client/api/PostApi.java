package net.sayaya.client.api;

import elemental2.core.JsArray;
import elemental2.dom.RequestInit;
import elemental2.dom.URLSearchParams;
import elemental2.promise.Promise;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.client.data.Post;
import net.sayaya.client.data.PostRequest;
import net.sayaya.client.data.Progress;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

import static elemental2.core.Global.JSON;

@Singleton
public class PostApi {
    private final FetchApi fetchApi;
    private final BehaviorSubject<Progress> progress;
    @Inject PostApi(FetchApi fetchApi, BehaviorSubject<Progress> progress) {
        this.fetchApi = fetchApi;
        this.progress = progress;
    }
    public Promise<CatalogItem[]> search(int page, int limit, String sortBy, boolean asc) {
        RequestInit request = RequestInit.create();
        request.setHeaders(new String[][] {
                new String[] {"Accept", "application/vnd.sayaya.v1+json"}
        });
        var params = new URLSearchParams();
        params.set("page", String.valueOf(page));
        params.set("limit", String.valueOf(limit));
        params.set("asc", String.valueOf(asc));
        params.set("sort_by", sortBy);
        //  params.set("title", "Test");
        progress.getValue().enabled(true).intermediate(true);
        return fetchApi.request("/posts?" + params, request).then(response->{
                    if(response.status==200)        return response.json();
                    else if(response.status==204)   return Promise.reject("Empty result");
                    return Promise.reject(response.statusText);
                }).then(json-> Promise.resolve((CatalogItem[])json))
                .finally_(()-> progress.getValue().enabled(false));
    }
    public Promise<String> save(PostRequest post) {
        RequestInit request = RequestInit.create();
        request.setMethod("PUT");
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/json"},
                new String[] {"Accept", "application/vnd.sayaya.v1"}
        });
        request.setBody(JSON.stringify(post));
        progress.getValue().enabled(true).intermediate(true);
        return fetchApi.request("/posts", request).then(response->{
            if(response.status==200)        return response.text();
            else if(response.status==204)   return Promise.reject("Empty result");
            return Promise.reject(response.statusText);
        }).finally_(()-> progress.getValue().enabled(false));
    }
}
