package net.sayaya.client.dom;

import elemental2.dom.HTMLLinkElement;
import elemental2.dom.HTMLSlotElement;
import elemental2.dom.MutationRecord;
import elemental2.dom.ShadowRootInit;
import jsinterop.annotations.JsType;
import net.sayaya.client.DaggerLoginComponent;
import net.sayaya.client.api.OAuthApi;

import static org.jboss.elemento.Elements.htmlElement;

@JsType
public class LogoutSceneElement extends CustomElement implements IsFrame {
    private OAuthApi api;
    public static void initialize(LogoutSceneElement instance, OAuthApi api) {
        instance.api = api;
    }
    @Override
    public void attach(MutationRecord mutationRecord) {
        api.logout();
    }
    @Override
    public void onHashChange(String hash) {

    }
}
