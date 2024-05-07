package net.sayaya.client.api;

import elemental2.dom.DomGlobal;
import lombok.experimental.UtilityClass;
import net.sayaya.client.data.Route;

import static elemental2.core.Global.JSON;

@UtilityClass
public class RouteApi {
    public void route(String url, boolean isFrame, boolean shouldReplace) {
        Route r = Route.builder().url(url).type(isFrame? Route.RouteType.FRAME : Route.RouteType.HOST).shouldReplace(shouldReplace).build();
        DomGlobal.window.parent.postMessage(JSON.stringify(r), "*");
    }
}
