package net.sayaya.client.content.component;

import lombok.experimental.Delegate;
import net.sayaya.client.content.dom.NavigationRailElement;
import net.sayaya.client.dom.ContainerBuilder;
import org.jboss.elemento.IsElement;

import static net.sayaya.client.dom.CustomElements.customContainer;

public class NavigationRailElementBuilder implements IsElement<NavigationRailElement> {
    public static NavigationRailElementBuilder nav() { return new NavigationRailElementBuilder(); }
    @Delegate private final ContainerBuilder<NavigationRailElement> _this = customContainer("sac-navigation-rail", NavigationRailElement.class).css("rail");
    @Delegate private final NavigationRailElement element = _this.element();
    public NavigationRailItemElementBuilder item() {
        var child = NavigationRailItemElementBuilder.item(this);
        add(child);
        return child;
    }
}
