package net.sayaya.client.edit;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import lombok.experimental.Delegate;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.elements.IconButtonElementBuilder.FilledTonalIconButtonElementBuilder;
import net.sayaya.ui.elements.TextFieldElementBuilder.OutlinedTextFieldElementBuilder;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.InputType;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static net.sayaya.ui.elements.ListElementBuilder.list;
import static net.sayaya.ui.elements.TextFieldElementBuilder.textField;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.label;

@Singleton
public class PublishElement implements IsElement<HTMLElement> {
    @Delegate private final HTMLContainerBuilder<HTMLDivElement> elem = div().css("publish");
    private final CardContainer card;
    private final FilledTonalIconButtonElementBuilder btnPublish = button().icon().filledTonal().toggle(true).css("btn-toggle")
            .add(icon().css("fa-sharp", "fa-light", "fa-pen-to-square", "icon"))
            .add(label().style("line-height: 1.7rem;").textContent("NOT PUBLISHED"))
            .toggle(icon().css("fa-sharp", "fa-light", "fa-up-to-line", "icon"))
            .toggle(label().style("line-height: 1.7rem;").textContent("PUBLISH"));
    private final OutlinedTextFieldElementBuilder thumbnail = textField().outlined().label("File").type(InputType.file);
    private final OutlinedTextFieldElementBuilder description = textField().outlined().label("Description").type(InputType.textarea).css("description").value("");
    @Inject PublishElement(@Named("post-card") HTMLContainerBuilder<HTMLElement> postCard,
                           TagsElement elemTags,
                           BehaviorSubject<CatalogItem> catalog) {
        card = Js.uncheckedCast(postCard.element());
        catalog.subscribe(this::update);
        var list = list().add(postCard.style("margin-bottom: 1rem;"))
                .divider()
                .add(btnPublish)
                .item().headline("Thumbnail").supportingText("Dropdown or upload img").end(thumbnail.element()).end()
                .add(elemTags.style("height: auto; margin: 1rem;"))
                .add(description);
        elem.add(list);
        thumbnail.on(EventType.change, evt->catalog.next(catalog.getValue().thumbnail(thumbnail.value())));
        btnPublish.onClick(evt->catalog.next(catalog.getValue().published(btnPublish.element().selected)));
        description.onChange(evt->catalog.next(catalog.getValue().description(description.element().value)));
    }
    private void update(CatalogItem catalog) {
        card.item(catalog);
        description.value(catalog.description!=null?catalog.description:"");
    }
    @JsType(isNative=true)
    private interface CardContainer {
        @JsMethod
        CatalogItem item(CatalogItem item);
    }
}
