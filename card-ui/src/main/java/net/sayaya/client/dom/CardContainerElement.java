package net.sayaya.client.dom;

import elemental2.dom.*;
import jsinterop.annotations.JsType;
import net.sayaya.client.component.CardElementBuilder;
import net.sayaya.client.data.CatalogItem;
import org.jboss.elemento.HTMLContainerBuilder;

import static elemental2.dom.DomGlobal.setTimeout;
import static org.jboss.elemento.Elements.*;

@JsType
public class CardContainerElement extends CustomElement {
    public static void initialize(CardContainerElement instance) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        instance.container = div().css("container");
        shadowRoot.append(
                htmlElement("style", HTMLStyleElement.class).add(CSS).element(),
                instance.container.element()
        );
    }
    private HTMLContainerBuilder<HTMLDivElement> container;
    public CardContainerElement add(CatalogItem[] items) {
        for(int i = 0; i < items.length; ++i) {
            var item = items[i];
            var card = new CardElementBuilder();
            container.add(card);
            setTimeout(c -> card.element().item(item), (i+1)*100);
        }
        return this;
    }
    private final static String CSS = "" +
            ".container {\n" +
            "    display: grid;\n" +
            "    grid-template-columns: repeat(auto-fit, minmax(19rem, 1fr));\n" +
            "    gap: 1rem;\n" +
            "    padding: 0.5rem;\n" +
            "    justify-content: space-between;\n" +
            "    overflow-x: hidden;\n" +
            "    overflow-y: auto;\n" +
            "}";
}
