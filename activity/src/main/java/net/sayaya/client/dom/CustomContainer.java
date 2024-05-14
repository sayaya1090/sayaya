package net.sayaya.client.dom;

import elemental2.dom.Node;
import org.jboss.elemento.Container;
import org.jboss.elemento.IsElement;
import org.jboss.elemento.TypedBuilder;

import java.util.function.Supplier;

public interface CustomContainer<E extends CustomElement, B extends TypedBuilder<E, B>> extends Container<E, B>, HasShadowElement<E, B>, IsElement<E> {
    default B add(Node node) {
        this.shadow().appendChild(node);
        return this.that();
    }
    default B add(Supplier<Node> supplier) {
        this.shadow().appendChild(supplier.get());
        return this.that();
    }
}

