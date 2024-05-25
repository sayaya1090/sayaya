package net.sayaya.client.content.component;

import elemental2.dom.HTMLDivElement;
import lombok.experimental.Delegate;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import static org.jboss.elemento.Elements.div;

public class FrameElementBuilder implements IsElement<HTMLDivElement> {
    @Delegate private final HTMLContainerBuilder<HTMLDivElement> _this = div().css("frame");
    public FrameElementBuilder() {}
}