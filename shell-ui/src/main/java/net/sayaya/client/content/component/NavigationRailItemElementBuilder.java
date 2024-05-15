package net.sayaya.client.content.component;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import net.sayaya.client.content.dom.NavigationRailItemElement;
import net.sayaya.client.dom.ContainerBuilder;
import net.sayaya.ui.elements.IconElementBuilder;
import net.sayaya.ui.elements.interfaces.HasHeadlineSlot;
import net.sayaya.ui.elements.interfaces.HasIconSlot;
import net.sayaya.ui.elements.interfaces.HasStartSlot;
import net.sayaya.ui.elements.interfaces.HasSupportingTextSlot;
import org.jboss.elemento.HasElement;
import org.jboss.elemento.IsElement;

import static net.sayaya.client.dom.CustomElements.customContainer;
import static org.jboss.elemento.Elements.div;

public class NavigationRailItemElementBuilder implements IsElement<NavigationRailItemElement>, HasElement<NavigationRailItemElement, NavigationRailItemElementBuilder>,
        HasIconSlot<NavigationRailItemElement, NavigationRailItemElementBuilder>, HasHeadlineSlot<NavigationRailItemElement, NavigationRailItemElementBuilder>,
        HasSupportingTextSlot<NavigationRailItemElement, NavigationRailItemElementBuilder>, HasStartSlot<NavigationRailItemElement, NavigationRailItemElementBuilder> {
    public static NavigationRailItemElementBuilder item(NavigationRailElementBuilder parent) {
        return new NavigationRailItemElementBuilder(parent);
    }
    private final ContainerBuilder<NavigationRailItemElement> _this = customContainer("sac-navigation-rail-item", NavigationRailItemElement.class).css("item");
    private final NavigationRailElementBuilder parent;
    public NavigationRailItemElementBuilder(NavigationRailElementBuilder parent) {
        this.parent = parent;
    }
    public NavigationRailElementBuilder end() {
        return parent;
    }
    public NavigationRailItemElementBuilder icon(Element icon) {
        icon.setAttribute("slot", "menu-collapse-icon");
        this.add(icon);
        return this;
    }
    public NavigationRailItemElementBuilder icon(IconElementBuilder icon) {
        return this.icon(icon.element());
    }
    public NavigationRailItemElementBuilder select(boolean selected) {
        element().select(selected);
        return this;
    }
    public NavigationRailItemElementBuilder start(Element icon) {
        icon.setAttribute("slot", "menu-expand-icon");
        this.add(icon);
        return this;
    }
    public NavigationRailItemElementBuilder trailingSupportingText(String supportingText) {
        return trailingSupportingText(div().add(supportingText));
    }
    public NavigationRailItemElementBuilder trailingSupportingText(IsElement<? extends HTMLElement> element) {
        return trailingSupportingText(element.element());
    }
    public NavigationRailItemElementBuilder trailingSupportingText(HTMLElement element) {
        element.setAttribute("slot", "trailing-supporting-text");
        add(element);
        return this;
    }
    @Override public NavigationRailItemElementBuilder that() {
        return this;
    }
    @Override
    public NavigationRailItemElement element() {
        return _this.element();
    }
}
