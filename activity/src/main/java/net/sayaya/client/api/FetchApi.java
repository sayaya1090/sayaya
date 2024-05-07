package net.sayaya.client.api;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import elemental2.promise.Promise;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FetchApi {
    public Promise<Response> request(String url) {
        return request(url, null);
    }
    public Promise<Response> request(String url, RequestInit param) {
        return DomGlobal.fetch(url, param).then(response -> {
            if (response.status == 401) DomGlobal.location.assign("login?redirectUrl=" + Global.encodeURIComponent(DomGlobal.location.href));
            return Promise.resolve(response);
        });
    }
}
