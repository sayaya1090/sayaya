package net.sayaya.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import elemental2.dom.DomGlobal;
import net.sayaya.client.dom.ConsoleElement;
import net.sayaya.client.dom.CustomElements;
import net.sayaya.client.dom.LoginElement;
import net.sayaya.client.dom.LoginSceneElement;

import static net.sayaya.client.dom.CustomElements.customContainer;
import static org.jboss.elemento.Elements.body;

public class Login implements EntryPoint {
    @Override
    public void onModuleLoad() {
        CustomElements.define("sac-console", ConsoleElement.class, ConsoleElement::initialize);
        CustomElements.define("sac-login-box", LoginElement.class, LoginElement::initialize);
        CustomElements.define("sac-login-scene", LoginSceneElement.class, LoginSceneElement::initialize);
        body().add(customContainer("sac-login-scene", LoginSceneElement.class).element().prepare());

    }
}
