package net.sayaya.client.console;

import com.google.gwt.core.client.EntryPoint;
import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import net.sayaya.client.component.ConsoleElementBuilder;
import net.sayaya.client.dom.ConsoleElement;
import net.sayaya.client.dom.CustomElements;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static org.jboss.elemento.Elements.body;

public class ConsoleTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        CustomElements.define("sac-console", ConsoleElement.class, ConsoleElement::initialize);
        String msg;
        var hash = DomGlobal.location.hash;
        if(hash!=null && hash.contains("#")) msg = Global.decodeURI(hash.substring(1));
        else msg = "Hello, World!";

        var console = new ConsoleElementBuilder();
        var type = button().filled().id("type").add("Type");
        type.onClick(evt->console.element().type(msg, false));
        var print = button().filled().id("print").add("Print");
        print.onClick(evt->console.print(msg));
        var clear = button().filled().id("clear").add("Clear");
        clear.onClick(evt->console.element().shadowRoot.getElementById("console").innerHTML = "");
        body().add(type).add(print).add(clear).add(console.id("console"));
    }
}
