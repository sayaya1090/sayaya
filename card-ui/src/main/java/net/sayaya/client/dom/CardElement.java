package net.sayaya.client.dom;

import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import elemental2.core.JsDate;
import elemental2.dom.*;
import jsinterop.annotations.JsType;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.ui.elements.ChipsElementBuilder;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.HTMLElementBuilder;

import static net.sayaya.ui.elements.ChipsElementBuilder.chips;
import static org.jboss.elemento.Elements.*;

@JsType
public class CardElement extends CustomElement {
    public static void initialize(CardElement instance) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        instance.container = div();
        instance.lblImage = img().attr("src", "https://letsenhance.io/static/8f5e523ee6b2479e26ecc91b9c25261e/6e61b/MainAfter.avif");
        instance.lblTitle = strong();
        instance.lblDescription = div();
        instance.lblAuthor = label();
        instance.lblTags = chips();
        instance.lblDate = label();
        shadowRoot.append(
                htmlElement("style", HTMLStyleElement.class).add(CSS).element(),
                instance.container.css("post-card")
                        .add(instance.lblImage)
                        .add(instance.lblTitle.css("title"))
                        .add(instance.lblDescription.css("desc"))
                        .add(div().css("meta")
                                .add(instance.lblAuthor.css("author"))
                                .add(instance.lblDate.css("date")))
                        .add(instance.lblTags).element()
        );
    }
    private HTMLContainerBuilder<HTMLDivElement> container;
    private HTMLElementBuilder<HTMLImageElement> lblImage;
    private HTMLContainerBuilder<HTMLElement> lblTitle;
    private HTMLContainerBuilder<HTMLDivElement> lblDescription;
    private HTMLContainerBuilder<HTMLLabelElement> lblAuthor;
    private ChipsElementBuilder lblTags;
    private HTMLContainerBuilder<HTMLLabelElement> lblDate;
    public CardElement item(CatalogItem item) {
        lblTitle.element().textContent = item.title;
        lblDescription.element().textContent = item.description;
        lblTags.element().innerHTML = "";
        if(item.tags!=null) for(var tag: item.tags) lblTags.assist().label(tag);
        lblAuthor.element().innerHTML = "written by " + item.author;
        lblDate.element().innerHTML = "Posted in: " + toLocalDate();
        return this;
    }
    private String toLocalDate() {
        JsDate jsDate = new JsDate();
        return jsDate.toLocaleDateString();
    }
    private final static String CSS = "" +
            ".post-card {\n" +
            "    display: flex;\n" +
            "    flex-direction: column;\n" +
            "    max-width: 20rem;\n" +
            "    min-width: 13rem;\n" +
            "    max-height: 30rem;\n" +
            "    padding: 1rem;\n" +
            "    cursor: pointer;\n" +
            "    margin-left: auto;\n" +
            "    margin-right: auto;\n" +
            "    border-radius: 0.5rem;\n" +
            "    font-family: \"Source Serif 4\", \"Noto Sans KR\", sans-serif;\n" +
            "}\n" +
            ":host-context(:root[color-theme='light']) .post-card {\n" +
            "    transition: box-shadow 300ms cubic-bezier(0.4, 0, 0.2, 1);\n" +
            "}\n" +
            ":host-context(:root[color-theme='light']) .post-card:hover {\n" +
            "    box-shadow: rgba(60, 0, 0, 0.23) 3px 3px 6px;\n" +
            "}\n" +
            ":host-context(:root[color-theme='dark']) .post-card {\n" +
            "    background: var(--md-sys-color-surface-container);\n" +
            "    transition: background 300ms cubic-bezier(0.4, 0, 0.2, 1);\n" +
            "}\n" +
            ":host-context(:root[color-theme='dark']) .post-card:hover {\n" +
            "    background: var(--md-sys-color-surface-container-high);\n" +
            "}\n" +
            ".post-card img {\n" +
            "    mask: linear-gradient(to top, transparent 0, black 4rem);\n" +
            "    min-height: 2rem;\n" +
            "}\n" +
            ".post-card .title {\n" +
            "    margin-top: -2rem;\n" +
            "    text-align: center;\n" +
            "    overflow-wrap: break-word;\n" +
            "    letter-spacing: -0.025em;\n" +
            "    word-break: break-all;\n" +
            "    line-height: var(--md-sys-typescale-headline-small-line-height);\n" +
            "    font-size: var(--md-sys-typescale-headline-small-size);\n" +
            "    display: inline;\n" +
            "    color: var(--md-sys-color-primary);\n" +
            "    text-decoration: none;\n" +
            "    transition: color 300ms cubic-bezier(0.4, 0, 0.2, 1);\n" +
            "}\n" +
            ".post-card:hover .title {\n" +
            "    color: var(--md-sys-color-secondary);\n" +
            "    text-decoration: underline;\n" +
            "}\n" +
            ".post-card .desc {\n" +
            "    padding-top: 1rem;\n" +
            "    color: var(--md-sys-color-on-surface-variant);\n" +
            "    word-break: break-all;\n" +
            "    overflow-wrap: break-word;\n" +
            "    text-align: justify;\n" +
            "    line-height: var(--md-sys-typescale-body-medium-line-height);\n" +
            "    font-size: var(--md-sys-typescale-body-medium-size);\n" +
            "}\n" +
            ".post-card .meta {\n" +
            "    display: flex; " +
            "    justify-content: space-between; " +
            "    padding-top: 1rem; " +
            "    color: var(--md-sys-color-on-surface-variant);\n" +
            "    line-height: var(--md-sys-typescale-body-medium-line-height);\n" +
            "    font-size: var(--md-sys-typescale-body-medium-size);\n" +
            "    text-overflow: ellipsis;\n" +
            "    white-space: nowrap;\n" +
            "    font-style: italic;\n" +
            "}\n" +
            ".post-card .author {\n" +
            "}\n" +
            ".post-card md-chip-set {\n" +
            "    padding-top: 1rem;\n" +
            "    text-align: right;\n" +
            "    flex-direction: row-reverse;\n" +
            "}\n" +
            ".post-card .date {\n" +
            "    text-align: right;\n" +
            "}";
}
