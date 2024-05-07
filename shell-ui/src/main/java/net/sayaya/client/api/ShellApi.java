package net.sayaya.client.api;

import elemental2.promise.Promise;
import jsinterop.base.Js;
import lombok.experimental.UtilityClass;
import net.sayaya.client.data.Menu;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ShellApi {
    public Promise<List<Menu>> findMenu() {
        return FetchApi.request("/shell/menu").then(response->{
            if(response.status==200)        return response.json().then(c->{
                Menu[] cast = Js.cast(c);
                return Promise.resolve(Arrays.stream(cast).collect(Collectors.toList()));
            });
            else if(response.status==204)   return Promise.resolve(new LinkedList<>());
            return Promise.reject(response.statusText);
        });
    }
}
