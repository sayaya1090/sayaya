package net.sayaya.client.edit;

import dagger.Provides;
import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import elemental2.dom.ResponseInit;
import elemental2.promise.Promise;
import net.sayaya.client.api.FetchApi;
import net.sayaya.client.data.GithubRepositories;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

import static elemental2.core.Global.JSON;
import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class PostEditTestModule {
    @Provides @Singleton @Named("url") static BehaviorSubject<String> provideContentUrl() {
        return behavior("");
    }
    @Provides @Singleton static FetchApi fetchApi() {
        return new FetchApi() {
            @Override public Promise<Response> request(String url, RequestInit param) {
                url = Global.decodeURI(url);
                if(url.startsWith("/post/repositories/user/Repo1/branches")) return Promise.resolve(branches(url, param));
                else if(url.startsWith("/post/repositories")) return Promise.resolve(repositories(url, param));
                else return Promise.resolve(Response.error());
            }
        };
    }
    private static Response repositories(String url, RequestInit param) {
        var headers = ResponseInit.create();
        headers.setHeaders(new String[][] {
                new String[] {"status", "200"}
        });
        var repositories = new GithubRepositories().owner("user").repos(new String[] {
                "Repo1", "Repo2", "Repo3"
        });
        return new Response(JSON.stringify(repositories), headers);
    }
    private static Response branches(String url, RequestInit param) {
        var headers = ResponseInit.create();
        headers.setHeaders(new String[][] {
                new String[] {"status", "200"}
        });
        return new Response(JSON.stringify(new String[] {"branch1", "branch2"}), headers);
    }
}

