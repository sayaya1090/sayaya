package net.sayaya.client.component;

import lombok.experimental.Delegate;
import net.sayaya.client.dom.CardElement;
import net.sayaya.client.dom.ContainerBuilder;
import org.jboss.elemento.IsElement;

import static net.sayaya.client.dom.CustomElements.customContainer;

public class CardElementBuilder implements IsElement<CardElement> {
    @Delegate private final ContainerBuilder<CardElement> _this = customContainer("sac-post-card", CardElement.class);
}
