package net.sayaya.client.component;

import lombok.experimental.Delegate;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.client.dom.CardElement;
import net.sayaya.client.dom.ContainerBuilder;
import org.jboss.elemento.IsElement;

import static net.sayaya.client.dom.CustomElements.customContainer;

public class CardElementBuilder implements IsElement<CardElement> {
    public static CardElementBuilder card() {
        return new CardElementBuilder();
    }
    @Delegate private final ContainerBuilder<CardElement> _this = customContainer("sac-post-card", CardElement.class);
    public CardElementBuilder item(CatalogItem item) {
        element().item(item);
        return this;
    }
}
