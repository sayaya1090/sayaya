package net.sayaya.client.api;

import elemental2.dom.*;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.client.component.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.body;
import static org.jboss.elemento.Elements.iframe;

@Singleton
public class OAuthApi {
    private final Logger logger;
    @Inject OAuthApi(Logger logger, FetchApi fetchApi) {
        this.logger = logger;
    }
    public Promise<Void> login(String provider) {
        String requestUrl = "login/oauth2/authorization/" + provider;
        return waitOAuth2LoginSuccess(requestUrl).then(param->{
            var url = "login/oauth2/code/" + provider + param;
            return waitAuthenticationCookie(url);
        }).then(redirect->{
            info("Logged in successfully.");
            DomGlobal.location.replace(redirect);
            return null;
        });
    }
    private Promise<String> waitOAuth2LoginSuccess(String requestUrl) {
        return new Promise<>((resolve, reject) -> {
            EventListener listener = new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    JsPropertyMap<String> data = Js.cast(((MessageEvent) evt).data);
                    if (data.has("code") && data.has("state")) {
                        String code = data.get("code");
                        String state = data.get("state");
                        info("Received access code from the provider.");
                        resolve.onInvoke("?code=" + code + "&state=" + state);
                    } else {
                        error("Invalid response");
                        reject.onInvoke("Invalid response");
                    }
                    evt.currentTarget.removeEventListener(evt.type, this);
                }
            };
            DomGlobal.window.addEventListener("message", listener);
            info("Open the authorization page.");
            DomGlobal.window.open(requestUrl, "_blank", "popup=true,width=600,height=800,status=0,menubar=0,toolbar=0");
        });
    }
    private Promise<String> waitAuthenticationCookie(String requestUrl) {
        var virtualFrame = iframe().style("display: none;");
        body().add(virtualFrame);
        virtualFrame.element().src = requestUrl;
        return new Promise<>((resolve, reject) -> virtualFrame.element().onload = evt-> {
            virtualFrame.element().remove();
            resolve.onInvoke(virtualFrame.element().src);
        });
    }
    private void info(String message) {
        logger.print("[   <font color='#009473'>OK</font>   ] " + message);
    }
    private void error(String message) {
        logger.print("[ <font color='#980001'>FAILED</font> ] " + message);
    }
}
