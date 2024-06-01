package net.sayaya.client.edit;

import elemental2.dom.HTMLDivElement;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.div;

@Singleton
public class PostEditScene extends HTMLContainerBuilder<HTMLDivElement> {
    @Inject public PostEditScene(TitleElement title) {
        super(div().add(title).element());
    }
}
