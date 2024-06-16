package net.sayaya.client.dom;

import elemental2.dom.DomGlobal;
import elemental2.dom.MutationRecord;
import jsinterop.annotations.JsType;
import net.sayaya.client.api.OAuthApi;
import net.sayaya.client.data.Progress;
import net.sayaya.rx.subject.BehaviorSubjectJs;

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
    @Override public void url(BehaviorSubjectJs<String> subject) {}
    @Override public void progress(Progress progress) {}
}
