package net.sayaya.client.content;

import elemental2.dom.HTMLDivElement;
import lombok.experimental.Delegate;
import net.sayaya.client.content.component.DrawerElementBuilder;
import net.sayaya.client.content.component.FrameElementBuilder;
import net.sayaya.client.content.component.FrameUpdater;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.div;

@Singleton
public class ContentElement implements IsElement<HTMLDivElement> {
    @Delegate private final HTMLContainerBuilder<HTMLDivElement> div;
    @Inject ContentElement(DrawerElementBuilder drawer, FrameUpdater frameUpdater) {
        this.div = div();
        div.id("content").style("display: flex; height: -webkit-fill-available; inset: 0;")
           .add(drawer).add(new FrameElementBuilder());
        frameUpdater.listen();
    }
}
