package net.sayaya.client.dom;

import org.jboss.elemento.Container;
import org.jboss.elemento.IsElement;
import org.jboss.elemento.TypedBuilder;

public interface CustomContainer<E extends CustomElement, B extends TypedBuilder<E, B>> extends Container<E, B>, HasShadowElement<E, B>, IsElement<E> {

}

