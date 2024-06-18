package net.sayaya.client;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.dom.*;

public class Login implements EntryPoint {
    @Override
    public void onModuleLoad() {
        var components = DaggerLoginComponent.create();
        CustomElements.define("sac-console", ConsoleElement.class, ConsoleElement::initialize);
        CustomElements.define("sac-login-box", LoginElement.class, LoginElement::initialize);
        CustomElements.define("sac-login-scene", LoginSceneElement.class, i->LoginSceneElement.initialize(i, components.console(), components.api()));
        CustomElements.define("sac-logout-scene", LogoutSceneElement.class, i->LogoutSceneElement.initialize(i, components.api()));
    }
}
