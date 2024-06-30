package net.sayaya.client.api;

import elemental2.dom.RequestInit;
import elemental2.dom.URLSearchParams;
import elemental2.promise.Promise;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.client.data.JsWindow;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ArticleApi {
    private final FetchApi fetchApi;
    @Inject ArticleApi(FetchApi fetchApi) {
        this.fetchApi = fetchApi;
    }
    public Promise<CatalogItem[]> search(int page, int limit, String sortBy, boolean asc, String... queries) {
        RequestInit request = RequestInit.create();
        request.setHeaders(new String[][] {
                new String[] {"Accept", "application/vnd.sayaya.v1+json"}
        });
        var params = new URLSearchParams();
        params.set("page", String.valueOf(page));
        params.set("limit", String.valueOf(limit));
        params.set("asc", String.valueOf(asc));
        params.set("sort_by", sortBy);
        if(queries!=null) for(String query: queries) params.append("query", query);

        JsWindow.progress.enabled(true).intermediate(true);
        return fetchApi.request("/blog?" + params, request).then(response->{
                    if(response.status==200)        return response.json();
                    else if(response.status==204)   return Promise.reject("Empty result");
                    return Promise.reject(response.statusText);
                }).then(json-> Promise.resolve((CatalogItem[])json))
                .finally_(()-> {
                    JsWindow.progress.enabled(false);
                });
    }
}
