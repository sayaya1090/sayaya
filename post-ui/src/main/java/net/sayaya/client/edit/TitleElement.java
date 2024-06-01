package net.sayaya.client.edit;

import elemental2.dom.HTMLElement;
import lombok.experimental.Delegate;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.elements.TextFieldElementBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.ui.elements.TextFieldElementBuilder.textField;

@Singleton
public class TitleElement implements IsElement<HTMLElement> {
    @Delegate private final TextFieldElementBuilder.OutlinedTextFieldElementBuilder elem = textField().outlined();
    @Inject TitleElement(@Named("title") BehaviorSubject<String> title) {
        elem.onChange(evt->title.next(elem.value()));
        title.subscribe(v->elem.value(v!=null?v:""));
    }
}
