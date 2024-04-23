package net.sayaya.client.console;

import com.google.gwt.core.client.EntryPoint;
import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import net.sayaya.client.component.Console;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static org.jboss.elemento.Elements.body;

public class ConsoleTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        String msg;
        var hash = DomGlobal.location.hash;
        if(hash!=null && hash.contains("#")) msg = Global.decodeURI(hash.substring(1));
        else msg = "Hello, World!";

        var console = new Console();
        var type = button().filled().id("type").add("Type");
        type.onClick(evt->console.type(msg));
        var print = button().filled().id("print").add("Print");
        print.onClick(evt->console.print(msg));
        var clear = button().filled().id("clear").add("Clear");
        clear.onClick(evt->console.element().innerHTML = "");
        body().add(type).add(print).add(clear).add(console.id("console"));
    }
}
