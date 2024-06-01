package net.sayaya.client.edit;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import lombok.experimental.Delegate;
import net.sayaya.rx.subject.BehaviorSubject;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.div;

@Singleton
public class PreviewElement implements IsElement<HTMLElement> {
    @Delegate private final HTMLContainerBuilder<HTMLDivElement> elem = div().css("html-preview", "markdown-body");
    @Inject PreviewElement(@Named("html") BehaviorSubject<String> html) {
        html.subscribe(evt-> elem.element().innerHTML = evt);
    }
}
