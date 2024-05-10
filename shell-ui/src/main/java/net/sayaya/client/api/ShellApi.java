package net.sayaya.client.api;

import elemental2.promise.Promise;
import jsinterop.base.Js;
import lombok.experimental.UtilityClass;
import net.sayaya.client.data.Menu;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class ShellApi {
    private final FetchApi fetchApi;
    @Inject ShellApi(FetchApi fetchApi) {
        this.fetchApi = fetchApi;
    }
    public Promise<List<Menu>> findMenu() {
        return fetchApi.request("/shell/menu").then(response->{
            if(response.status==200)        return response.json().then(c->{
                Menu[] cast = Js.cast(c);
                return Promise.resolve(Arrays.stream(cast).collect(Collectors.toList()));
            });
            else if(response.status==204)   return Promise.resolve(new LinkedList<>());
            return Promise.reject(response.statusText);
        });
    }
}
