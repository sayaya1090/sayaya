package net.sayaya.client.content.component;

import lombok.experimental.Delegate;
import net.sayaya.client.content.dom.ProgressElement;
import net.sayaya.client.dom.ContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import static net.sayaya.client.dom.CustomElements.customContainer;

@Singleton
public class ProgressElementBuilder implements IsElement<ProgressElement> {
    @Delegate private final ContainerBuilder<ProgressElement> _this = customContainer("sac-progress", ProgressElement.class);
    @Delegate private final ProgressElement element = _this.element();
    @Inject public ProgressElementBuilder() {}
}
