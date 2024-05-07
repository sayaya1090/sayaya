package net.sayaya.client.content;

import elemental2.dom.HTMLDivElement;
import lombok.experimental.Delegate;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.div;

@Singleton
public class ContentElement implements IsElement<HTMLDivElement> {
    @Delegate private final HTMLContainerBuilder<HTMLDivElement> div;
    @Inject ContentElement(DrawerElement drawer, FrameElement frame) {
        this.div = div();
        div.style("display: flex; position: absolute; inset: 0;")
           .add(drawer)
           .add(frame);
    }
}
