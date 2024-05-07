package net.sayaya.client.event;

import elemental2.dom.DomGlobal;
import net.sayaya.client.data.Message;
import net.sayaya.client.data.Route;
import net.sayaya.rx.subject.BehaviorSubject;

public class RouteHandler implements ModuleEventHandler {
    private final BehaviorSubject<String> contentUrl;
    private final String prefix = DomGlobal.window.location.protocol + "//" + DomGlobal.window.location.host;
    public RouteHandler(BehaviorSubject<String> contentUrl) {
        this.contentUrl = contentUrl;
        DomGlobal.window.onpopstate = evt->{
            String url = DomGlobal.location.href;
            if(url!=null && !url.isEmpty()) contentUrl.next(trimHtml(url));
            return null;
        };
    }
    @Override
    public boolean chk(String type) {
        return "route".equals(type);
    }
    @Override
    public void exec(Message msg) {
        Route route = (Route) msg;
        if(!route.url.startsWith(prefix)) route.url = prefix + route.url;
        if(Route.RouteType.FRAME.name().equalsIgnoreCase(route.param.toString())) contentUrl.next(trimHtml(route.url));
        else {
            if(route.shouldReplace) DomGlobal.window.history.replaceState(route, DomGlobal.document.title, route.url);
            else DomGlobal.window.history.pushState(route, DomGlobal.document.title, route.url);
        }
    }
    private static String trimHtml(String url) {
        int indexOfHtml = url.lastIndexOf(".html");
        if(indexOfHtml == -1) return url;
        else return url.substring(0, indexOfHtml) + url.substring(indexOfHtml + 5);
    }
}
