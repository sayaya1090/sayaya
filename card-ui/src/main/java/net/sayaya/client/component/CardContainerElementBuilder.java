package net.sayaya.client.component;

import lombok.experimental.Delegate;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.client.dom.CardContainerElement;
import net.sayaya.client.dom.ContainerBuilder;
import org.jboss.elemento.IsElement;

import java.util.List;

import static net.sayaya.client.dom.CustomElements.customContainer;

public class CardContainerElementBuilder implements IsElement<CardContainerElement> {
    public static CardContainerElementBuilder cards() {
        return new CardContainerElementBuilder();
    }
    @Delegate private final ContainerBuilder<CardContainerElement> _this = customContainer("sac-post-card-container", CardContainerElement.class);
    public CardContainerElementBuilder add(List<CatalogItem> items) {
        return add(items.stream().toArray(CatalogItem[]::new));
    }
    public CardContainerElementBuilder add(CatalogItem... items) {
        element().add(items);
        return this;
    }
}
