package net.sayaya.client.edit;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import lombok.experimental.Delegate;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.elements.ChipsElementBuilder;
import net.sayaya.ui.elements.TextFieldElementBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class TagsElement implements IsElement<HTMLElement> {
    private final TextFieldElementBuilder.OutlinedTextFieldElementBuilder input = TextFieldElementBuilder.textField().outlined().label("Tag").css("input");
    @Delegate private final ChipsElementBuilder chips = ChipsElementBuilder.chips();
    private final AtomicBoolean mtxInitialize = new AtomicBoolean(false);
    @Inject TagsElement(@Named("tags") BehaviorSubject<Set<String>> tags) {
        element().classList.add("tags");
        chips.element().append(input.element());
        input.element().setAttribute("contenteditable", "true");
        input.onChange(evt->{
            evt.preventDefault();
            evt.stopPropagation();
            var activeElement = DomGlobal.document.activeElement;
            var tag = input.value();
            if(tag!=null && tag.length() > 2) {
                tags.getValue().add(input.value());
                tags.next(tags.getValue());
            }
            if(activeElement==input.element()) input.element().focus();
            input.value("");
        });
        tags.subscribe(tag->{
            mtxInitialize.set(true);
            chips.element().innerHTML = "";
            chips.element().append(input.element());
            for(var t: tag) {
                var chip = chips.input().label(t);
                chip.element().addEventListener("DOMNodeRemoved", evt->{
                    if(mtxInitialize.get()) return;
                    tags.getValue().remove(t);
                    tags.next(tags.getValue());
                });
            }
            chips.element().append(input.element());
            mtxInitialize.set(false);
        });
    }
}
