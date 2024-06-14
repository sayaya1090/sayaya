package net.sayaya.client.dom;

import elemental2.dom.*;
import jsinterop.annotations.JsType;
import net.sayaya.client.api.OAuthApi;
import net.sayaya.client.component.ConsoleElementBuilder;
import net.sayaya.client.data.Progress;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.BehaviorSubjectJs;

import static net.sayaya.client.dom.CustomElements.customContainer;
import static org.jboss.elemento.Elements.*;

@JsType
public class LoginSceneElement extends CustomElement implements IsFrame {
    public static void initialize(LoginSceneElement instance, ConsoleElementBuilder console, OAuthApi api, BehaviorSubject<Progress> progress) {
        instance.console = console;
        instance.api = api;
        instance.subject = progress;
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        shadowRoot.append(
                htmlElement("link", HTMLLinkElement.class).attr("rel", "stylesheet").attr("href", "css/login.css").element(),
                htmlElement("slot", HTMLSlotElement.class).element()
        );
    }
    private ConsoleElementBuilder console;
    private OAuthApi api;
    private BehaviorSubject<Progress> subject;
    @Override
    public void attach(MutationRecord mutationRecord) {
        var elem = customContainer("sac-login-box", LoginElement.class);
        this.append(elem.element().console(console.element()).api(api));
        elem.element().attach(mutationRecord);
    }
    @Override
    public void onHashChange(String hash) {

    }
    @Override public void url(BehaviorSubjectJs<String> subject) {}
    @Override public void progress(Progress progress) {
        subject.next(progress);
    }
}
