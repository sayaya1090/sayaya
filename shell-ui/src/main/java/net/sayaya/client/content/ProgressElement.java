package net.sayaya.client.content;

import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLElement;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.elements.ProgressElementBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import static net.sayaya.ui.elements.ProgressElementBuilder.progress;

@Singleton
public class ProgressElement implements IsElement<HTMLElement> {
    private final ProgressElementBuilder.LinearProgressElementBuilder progress = progress().linear();
    @Inject ProgressElement(BehaviorSubject<Progress> subject) {
        progress.element().style.width = CSSProperties.WidthUnionType.of("100%");
        subject.subscribe(this::update);
    }
    private void update(Progress value) {
        if(!value.isEnabled()) {
            progress.element().style.opacity = CSSProperties.OpacityUnionType.of("0");
        } else {
            progress.element().style.opacity = CSSProperties.OpacityUnionType.of("100");
            progress.indeterminate(value.isIntermediate());
            progress.element().max = value.max();
            progress.value(value.current());
        }
    }
    @Override public HTMLElement element() {
        return progress.element();
    }
}
