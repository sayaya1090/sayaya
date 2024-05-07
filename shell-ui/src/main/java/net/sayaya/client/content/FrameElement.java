package net.sayaya.client.content;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLIFrameElement;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.iframe;

@Singleton
public class FrameElement extends HTMLContainerBuilder<HTMLIFrameElement> implements IsElement<HTMLIFrameElement> {
    @Inject FrameElement() {
        this(iframe(DomGlobal.document.getElementById("content")));
    }
    private final HTMLContainerBuilder<HTMLIFrameElement> elem;
    private FrameElement(HTMLContainerBuilder<HTMLIFrameElement> elem) {
        super(elem.element());
        this.elem = elem;
    }
    @Override
    public FrameElement that() {
        return this;
    }
}