package net.sayaya.client.dom;

import elemental2.dom.ShadowRoot;
import org.jboss.elemento.*;

import java.util.Objects;

public class ContainerBuilder<E extends CustomElement> implements HasHTMLElement<E, ContainerBuilder<E>>, Finder<E>, CustomContainer<E, ContainerBuilder<E>> {
    private final E element;
    public ContainerBuilder(E element) {
        this.element = Objects.requireNonNull(element, "element required");
    }
    public ContainerBuilder<E> that() {
        return this;
    }
    public E element() {
        return this.element;
    }
    @Override
    public ShadowRoot shadow() {
        return element.shadowRoot;
    }
}
