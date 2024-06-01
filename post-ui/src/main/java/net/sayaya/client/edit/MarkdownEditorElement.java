package net.sayaya.client.edit;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import lombok.experimental.Delegate;
import net.sayaya.client.edit.handler.KeyEventHandler;
import net.sayaya.client.edit.handler.PasteEventHandler;
import net.sayaya.rx.subject.BehaviorSubject;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import java.util.Set;

import static net.sayaya.client.util.Debounce.debounce;
import static org.jboss.elemento.Elements.div;

@Singleton
public class MarkdownEditorElement implements IsElement<HTMLElement> {
    @Delegate private final HTMLContainerBuilder<HTMLDivElement> elem = div();
    @Inject MarkdownEditorElement(
            MarkdownEditorControllerElement ctr,
            MarkdownEditorInputElement ipt,
            ImageContainer images,
            Set<KeyEventHandler> keyEventHandlers,
            Set<PasteEventHandler> pasteEventHandlers,
            @Named("markdown") BehaviorSubject<String> markdown) {
        this.css("markdown-editor").add(ctr).add(ipt).add(images);
        var updateMarkdown = debounce(()->markdown.next(ipt.element().value), 1000);
        ipt.on(EventType.keyup,     evt->updateMarkdown.run());
        ipt.on(EventType.keydown,   evt->{ for(var handler: keyEventHandlers) handler.handle(evt, ipt.element());});
        ipt.on(EventType.paste,     evt->{ for(var handler: pasteEventHandlers) handler.handle(evt, ipt.element()); });
        ipt.on(EventType.change,    evt->markdown.next(ipt.element().value));
        markdown.subscribe(m->ipt.value(m!=null?m:""));
    }
}
