package net.sayaya.client.legacy.content;

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
    @Inject ContentElement(DrawerElement drawer, FrameUpdater frameUpdater) {
        this.div = div();
        div.id("content").style("display: flex; position: absolute; inset: 0;")
           .add(drawer);
        frameUpdater.listen();
    }
}
