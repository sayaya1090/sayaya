package net.sayaya.client.component;

import lombok.experimental.Delegate;
import net.sayaya.client.dom.ContainerBuilder;
import net.sayaya.client.dom.ConsoleElement;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import static net.sayaya.client.dom.CustomElements.customContainer;

@Singleton
public class ConsoleElementBuilder implements IsElement<ConsoleElement>, Logger {
    @Delegate private final ContainerBuilder<ConsoleElement> _this = customContainer("sac-console", ConsoleElement.class).style("height: 0rem; transition: height 300ms ease 0ms; margin: auto;");
    @Inject public ConsoleElementBuilder() {}
    @Override
    public void print(String text) {
        element().print(text, false);
    }
}
