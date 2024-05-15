package net.sayaya.client.content.dom;

import elemental2.dom.*;
import jsinterop.annotations.JsType;
import net.sayaya.client.dom.CustomElement;
import net.sayaya.ui.elements.IconButtonElementBuilder;
import org.jboss.elemento.HTMLContainerBuilder;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static net.sayaya.ui.elements.FocusRingElementBuilder.focusRing;
import static net.sayaya.ui.elements.RippleElementBuilder.ripple;
import static org.jboss.elemento.Elements.*;

@JsType
public class NavigationRailItemElement extends CustomElement {
    public static void initialize(NavigationRailItemElement instance) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        var item = htmlContainer("md-item", HTMLElement.class)
                .add(htmlElement("slot", HTMLSlotElement.class).css("icon").attr("name", "menu-expand-icon").attr("slot", "start"))
                .add(htmlElement("slot", HTMLSlotElement.class).attr("name", "headline").attr("slot", "headline"))
                .add(htmlElement("slot", HTMLSlotElement.class).attr("name", "supporting-text").attr("slot", "supporting-text"))
                .add(htmlElement("slot", HTMLSlotElement.class).attr("name", "trailing-supporting-text").attr("slot", "trailing-supporting-text").style("color: var(--md-list-item-label-text-color);"));
        item.element().setAttribute("multiline", true);
        instance.collapse = button().icon().css("collapse")
                .add(htmlElement("slot", HTMLSlotElement.class).attr("name", "menu-collapse-icon"))
                .toggle(true);
        instance.expand = li().css("expand").attr("tabindex", "0");
        shadowRoot.append(
                htmlElement("style", HTMLStyleElement.class).add(CSS).element(),
                instance.collapse.element(),
                instance.expand.add(ripple()).add(focusRing()).add(item).element()
        );
    }
    private IconButtonElementBuilder.PlainIconButtonElementBuilder collapse;
    private HTMLContainerBuilder<HTMLLIElement> expand;
    public void expand() {
        expand.style("display", "flex");
        collapse.style("display", "none");
    }
    public void collapse() {
        expand.style("display", "none");
        collapse.style("display", "inherit");
    }
    public void select(boolean value) {
        collapse.element().selected = value;
        if(value) expand.element().setAttribute("selected", true);
        else expand.element().removeAttribute("selected");
    }
    private final static String CSS = "" +
            ":host {" +
            "    color: unset;\n" +
            "    cursor: pointer;\n" +
            "    display: flex;\n" +
            "    flex-direction: column;\n" +
            "    outline: none;\n" +
            "    padding: 8px 0;\n" +
            "    position: relative;\n" +
            "    -webkit-tap-highlight-color: rgba(0, 0, 0, 0);\n" +
            "    --md-list-item-label-text-font: var(--md-sys-typescale-headline-large-font);\n" +
            "    --md-ripple-hover-color: var(--md-list-item-hover-state-layer-color, var(--md-sys-color-on-surface, #1d1b20));\n" +
            "    --md-ripple-hover-opacity: var(--md-list-item-hover-state-layer-opacity, 0.08);\n" +
            "    --md-ripple-pressed-color: var(--md-list-item-pressed-state-layer-color, var(--md-sys-color-on-surface, #1d1b20));\n" +
            "    --md-ripple-pressed-opacity: var(--md-list-item-pressed-state-layer-opacity, 0.12);\n" +
            "}\n" +
            ".expand {\n" +
            "    border-radius: 2rem;\n" +
            "    position: relative;\n" +
            "    display: none;\n" +
            "    flex: 1;\n" +
            "    max-width: inherit;\n" +
            "    min-width: inherit;\n" +
            "    outline: none;\n" +
            "    -webkit-tap-highlight-color: rgba(0, 0, 0, 0);\n" +
            "    width: 100%;\n" +
            "}\n" +
            ".expand[selected] {" +
            "    --md-list-item-label-text-color: var(--md-sys-color-primary);\n" +
            "    --md-list-item-leading-icon-color: var(--md-list-item-label-text-color);\n" +
            "}\n" +
            ".collapse {\n" +
            "    display: inherit;\n" +
            "    margin: 10px 8px;\n" +
            "}\n" +
            "md-item {\n" +
            "    border-radius: inherit;\n" +
            "    flex: 1;\n" +
            "    height: 100%;\n" +
            "    color: var(--md-list-item-label-text-color, var(--md-sys-color-on-surface, #1d1b20));\n" +
            "    font-family: var(--md-list-item-label-text-font, var(--md-sys-typescale-body-large-font, var(--md-ref-typeface-plain, Roboto)));\n" +
            "    font-size: var(--md-list-item-label-text-size, var(--md-sys-typescale-body-large-size, 1rem));\n" +
            "    line-height: var(--md-list-item-label-text-line-height, var(--md-sys-typescale-body-large-line-height, 1.5rem));\n" +
            "    font-weight: var(--md-list-item-label-text-weight, var(--md-sys-typescale-body-large-weight, var(--md-ref-typeface-weight-regular, 400)));\n" +
            "    min-height: var(--md-list-item-one-line-container-height, 60px);\n" +
            "    padding-top: var(--md-list-item-top-space, 12px);\n" +
            "    padding-bottom: var(--md-list-item-bottom-space, 12px);\n" +
            "    padding-inline-start: var(--md-list-item-leading-space, 16px);\n" +
            "    padding-inline-end: var(--md-list-item-trailing-space, 16px);\n" +
            "}\n" +
            ".icon {" +
            "    font-size: var(--md-icon-size, 24px);\n" +
            "    width: var(--md-icon-size, 24px);\n" +
            "    height: var(--md-icon-size, 24px);\n" +
            "    color: inherit;\n" +
            "    font-variation-settings: inherit;\n" +
            "    font-weight: 400;\n" +
            "    font-family: var(--md-icon-font, Material Symbols Outlined);\n" +
            "    display: inline-flex;\n" +
            "    font-style: normal;\n" +
            "    place-items: center;\n" +
            "    place-content: center;\n" +
            "    line-height: 1;\n" +
            "    overflow: hidden;\n" +
            "    letter-spacing: normal;\n" +
            "    text-transform: none;\n" +
            "    user-select: none;\n" +
            "    white-space: nowrap;\n" +
            "    word-wrap: normal;\n" +
            "    flex-shrink: 0;\n" +
            "    -webkit-font-smoothing: antialiased;\n" +
            "    text-rendering: optimizeLegibility;\n" +
            "    -moz-osx-font-smoothing: grayscale;" +
            "}";
}
