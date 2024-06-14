package net.sayaya.client.api;

import elemental2.dom.DomGlobal;
import elemental2.dom.RequestInit;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import net.sayaya.client.data.GithubAppConfig;
import net.sayaya.client.data.GithubConfigRequest;
import net.sayaya.client.data.GithubRepositories;
import net.sayaya.client.data.GithubRepositoryConfig;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

import static elemental2.core.Global.JSON;

@Singleton
public class GithubApi {
    private final FetchApi fetchApi;
    @Inject GithubApi(FetchApi fetchApi) {
        this.fetchApi = fetchApi;
    }
    public Promise<GithubRepositoryConfig> findGithubRepositoryConfig() {
        RequestInit request = RequestInit.create();
        request.setHeaders(new String[][] {
                new String[] {"Accept", "application/vnd.sayaya.v1+json"}
        });
        return fetchApi.request("/post/destination", request).then(response->{
            if(response.status==200)        return response.json().then(Js::cast);
            else if(response.status==204)   return Promise.resolve((GithubRepositoryConfig)null);
            return Promise.reject(response.statusText);
        });
    }
    public Promise<GithubRepositories> findGithubRepository(GithubAppConfig appConfig) {
        RequestInit request = RequestInit.create();
        if(appConfig!=null) {
            request.setMethod("POST");
            request.setBody(JSON.stringify(appConfig));
        }
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/json"},
                new String[] {"Accept", "application/vnd.sayaya.v1+json"}
        });
        return fetchApi.request("/post/repositories", request).then(response->{
            if(response.status==200)        return response.json().then(Js::cast);
            else if(response.status==204)   return Promise.resolve((GithubRepositories)null);
            return Promise.reject(response.statusText);
        });
    }
    public Promise<List<String>> findGithubBranch(GithubAppConfig appConfig, String owner, String repository) {
        RequestInit request = RequestInit.create();
        if(appConfig!=null) {
            request.setMethod("POST");
            request.setBody(JSON.stringify(appConfig));
        }
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/json"},
                new String[] {"Accept", "application/vnd.sayaya.v1+json"}
        });
        return fetchApi.request("/post/repositories/" + owner + "/" + repository + "/branches", request).then(response->{
            if(response.status==200) return response.json().then(json->{
                Object[] cast = Js.asArray(json);
                List<String> repos = new ArrayList<>();
                for(Object repo: cast) repos.add(repo.toString());
                return Promise.resolve(repos);
            });
            else if(response.status==204) return Promise.resolve(new ArrayList());
            return Promise.reject(response.statusText);
        });
    }
    public Promise<GithubRepositoryConfig> save(GithubConfigRequest config) {
        RequestInit request = RequestInit.create();
        request.setMethod("PUT");
        request.setHeaders(new String[][] {
                new String[] {"Content-Type", "application/json"},
                new String[] {"Accept", "application/vnd.sayaya.v1+json"}
        });
        request.setBody(JSON.stringify(config));
        return fetchApi.request("/post/destination", request).then(response->{
            if(response.status==200)        return response.json().then(Js::cast);
            else if(response.status==204)   return Promise.resolve((GithubRepositoryConfig)null);
            return Promise.reject(response.statusText);
        });
    }
}
