package net.sayaya.client.edit;

import elemental2.dom.HTMLElement;
import lombok.experimental.Delegate;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.elements.TextFieldElementBuilder.OutlinedTextFieldElementBuilder;
import org.jboss.elemento.InputType;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.ui.elements.TextFieldElementBuilder.textField;

@Singleton
public class MarkdownEditorInputElement implements IsElement<HTMLElement> {
    @Delegate private final OutlinedTextFieldElementBuilder elem = textField().outlined().type(InputType.textarea).id("content");
    @Inject MarkdownEditorInputElement(@Named("is-publish-mode") BehaviorSubject<Boolean> isPublishMode) {
        isPublishMode.subscribe(elem::disable);
        this.style("width:100%; height:-webkit-fill-available;");
    }
}
