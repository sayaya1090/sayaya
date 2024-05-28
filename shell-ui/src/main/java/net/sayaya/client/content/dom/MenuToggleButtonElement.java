package net.sayaya.client.content.dom;

import elemental2.dom.HTMLStyleElement;
import elemental2.dom.ShadowRootInit;
import jsinterop.annotations.JsType;
import net.sayaya.client.dom.CustomElement;
import net.sayaya.ui.svg.dom.SVGElement;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static net.sayaya.ui.svg.elements.SvgBuilder.svg;
import static net.sayaya.ui.svg.elements.SvgPathBuilder.path;
import static org.jboss.elemento.Elements.htmlElement;

@JsType
public class MenuToggleButtonElement extends CustomElement {
    public static void initialize(MenuToggleButtonElement instance) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);

        instance.icon = svg().viewBox(0, 0, 100, 100)
                .add(path().css("line").d("M 80,29 H 20 C 20,29 5.501161,28.817352 5.467013,66.711331 5.456858,77.980673 9.033919,81.670246 14.740827,81.668997 20.447739,81.667751 25,75 25,75 L 75,25"))
                .add(path().css("line", "line-middle").d("M 80,50 H 20"))
                .add(path().css("line").d("M 80,71 H 20 C 20,71 5.501161,71.182648 5.467013,33.288669 5.456858,22.019327 9.033919,18.329754 14.740827,18.331003 20.447739,18.332249 25,25 25,25 L 75,75"))
                .element();
        shadowRoot.append(
                htmlElement("style", HTMLStyleElement.class).add(CSS).element(),
                button().icon().add(instance.icon).ariaLabel("Menu").element()
        );
    }
    private SVGElement icon;
    public void open() {
        icon.classList.add("opened");
    }
    public void close() {
        icon.classList.remove("opened");
    }
    private final static String CSS = "" +
            ".opened {\n" +
            "    transform: scaleX(-1);\n" +
            "}\n" +
            ".line {\n" +
            "    fill: none;\n" +
            "    stroke: var(--_icon-color);\n" +
            "    transition: stroke-dasharray 500ms cubic-bezier(.4,0,.2,1),\n" +
            "    stroke-dashoffset 500ms cubic-bezier(.4,0,.2,1);\n" +
            "    stroke-dasharray: 60 207;\n" +
            "    stroke-width: 5;\n" +
            "    transform: scale(1.3);\n" +
            "    transform-origin: 50%;\n" +
            "}\n" +
            ".line-middle {\n" +
            "    stroke-dasharray: 60 60;\n" +
            "}\n" +
            ".opened .line {\n" +
            "    stroke-dasharray: 90 207;\n" +
            "    stroke-dashoffset: -134;\n" +
            "}\n" +
            ".opened .line-middle {\n" +
            "    stroke-dasharray: 1 60;\n" +
            "    stroke-dashoffset: -30;\n" +
            "}";
}