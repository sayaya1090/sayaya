package net.sayaya.client.content.dom;

import elemental2.dom.*;
import jsinterop.annotations.JsType;
import net.sayaya.client.dom.CustomElement;

import static org.jboss.elemento.Elements.*;

@JsType
public class NavigationRailElement extends CustomElement {
    public static void initialize(NavigationRailElement instance) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        shadowRoot.append(
                htmlElement("slot", HTMLSlotElement.class).element()
        );
    }
    public void clear() {
        querySelectorAll("sac-navigation-rail-item").forEach((a, b, c)-> {
            a.remove();
            return null;
        });
    }
    public void expand() {
        setAttribute("expand", true);
        removeAttribute("collapse");
        removeAttribute("hide");
        querySelectorAll("sac-navigation-rail-item").forEach((a, b, c)->{
            NavigationRailItemElement cast = (NavigationRailItemElement)a;
            cast.expand();
            return null;
        });
    }
    public void collapse() {
        removeAttribute("expand");
        setAttribute("collapse", true);
        removeAttribute("hide");
        querySelectorAll("sac-navigation-rail-item").forEach((a, b, c)->{
            NavigationRailItemElement cast = (NavigationRailItemElement)a;
            cast.collapse();
            return null;
        });
    }
    public void hide() {
        removeAttribute("expand");
        removeAttribute("collapse");
        setAttribute("hide", true);
    }
    private final static String CSS = "";
}
