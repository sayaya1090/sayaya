package net.sayaya.client.dom;

import elemental2.dom.MutationRecord;
import jsinterop.annotations.JsType;
import net.sayaya.client.api.OAuthApi;

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
}
