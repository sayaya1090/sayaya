package net.sayaya.client.dom;

import elemental2.dom.HTMLLinkElement;
import elemental2.dom.HTMLSlotElement;
import elemental2.dom.MutationRecord;
import elemental2.dom.ShadowRootInit;
import jsinterop.annotations.JsType;
import net.sayaya.client.api.OAuthApi;
import net.sayaya.client.component.ConsoleElementBuilder;

import static net.sayaya.client.dom.CustomElements.customContainer;
import static org.jboss.elemento.Elements.htmlElement;

@JsType
public class LoginSceneElement extends CustomElement implements IsFrame {
    public static void initialize(LoginSceneElement instance, ConsoleElementBuilder console, OAuthApi api) {
        instance.console = console;
        instance.api = api;
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
    @Override
    public void attach(MutationRecord mutationRecord) {
        var elem = customContainer("sac-login-box", LoginElement.class);
        this.append(elem.element().console(console.element()).api(api));
        elem.element().attach(mutationRecord);
    }
}
