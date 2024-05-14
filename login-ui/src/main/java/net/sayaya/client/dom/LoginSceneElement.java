package net.sayaya.client.dom;

import elemental2.dom.HTMLLinkElement;
import elemental2.dom.HTMLSlotElement;
import elemental2.dom.ShadowRootInit;
import jsinterop.annotations.JsType;
import net.sayaya.client.DaggerLoginComponent;

import static org.jboss.elemento.Elements.*;

@JsType
public class LoginSceneElement extends CustomSceneElement<LoginSceneElement> {
    public static void initialize(LoginSceneElement instance) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        shadowRoot.append(
                htmlElement("link", HTMLLinkElement.class).attr("rel", "stylesheet").attr("href", "css/login.css").element(),
                htmlElement("slot", HTMLSlotElement.class).element()
        );
    }
    @Override
    public LoginSceneElement draw() {
        this.append(DaggerLoginComponent.create().login().element());
        return this;
    }
}
