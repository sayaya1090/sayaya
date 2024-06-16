package net.sayaya.client.list;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.elements.MenuElementBuilder;
import net.sayaya.ui.elements.SelectElementBuilder;
import net.sayaya.ui.elements.TextFieldElementBuilder;
import org.jboss.elemento.EventCallbackFn;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static net.sayaya.ui.elements.SelectElementBuilder.select;
import static net.sayaya.ui.elements.TextFieldElementBuilder.textField;
import static org.jboss.elemento.Elements.div;

@Singleton
public class PostListScene extends HTMLContainerBuilder<HTMLDivElement> {
    private final TextFieldElementBuilder.OutlinedTextFieldElementBuilder iptSearchBox = textField().outlined().label("Search");
    private final SelectElementBuilder.OutlinedSelectElementBuilder iptOrderBy = select().outlined().label("Sort by")
            .option().value("title").headline("Title").end()
            .option().value("createdAt").headline("Created").end()
            .option().value("updatedAt").headline("Updated").end()
            .required(true);
    private final SelectElementBuilder.OutlinedSelectElementBuilder iptOrder = select().outlined().label("")
            .option().value("true").headline("Asc").end()
            .option().value("false").headline("Desc").end()
            .required(true);
    private final HTMLContainerBuilder<HTMLElement> container;
    private final MenuElementBuilder.TopMenuElementBuilder menu = MenuElementBuilder.menu().positioning(MenuElementBuilder.Position.Fixed);
    @Inject public PostListScene(
            HTMLContainerBuilder<HTMLElement> container,
            BehaviorSubject<CatalogItem[]> posts,
            @Named("sort-by") BehaviorSubject<String> sortBy,
            @Named("sort") BehaviorSubject<String> sort,
            @Named("url") BehaviorSubject<String> contentUrl) {
        super(div().element());
        this.container = container;
        this.style("display: flex; flex-direction: column; height: 100%;")
                .add(div().style("padding: 0.5rem; display: flex; justify-content: space-between; align-items: center; gap: 0.5rem;")
                        .add(iptSearchBox.style("flex-grow: 1;")).add(iptOrderBy).add(iptOrder))
                .add(container)
                .add(menu);
        sortBy.subscribe(v->iptOrderBy.element().value = v);
        sort.subscribe(v->iptOrder.element().value = v);
        iptOrderBy.onChange(evt->sortBy.next(iptOrderBy.value()));
        iptOrder.onChange(evt->sort.next(iptOrder.value()));
        menu.item().start(icon().css("fa-sharp", "fa-light", "fa-pen-to-square").style("font-size: 1rem;")).headline("Edit").on(EventType.click, evt->{
            evt.preventDefault();
            contentUrl.next("/post#" + selected.id);
        }).end().item().start(icon().css("fa-sharp", "fa-light", "fa-up-to-line").style("font-size: 1rem;")).headline("Publish").on(EventType.click, evt->{
            evt.preventDefault();
            contentUrl.next("/post#" + selected.id + "/publish");
        }).end().item().start(icon().css("fa-sharp", "fa-light", "fa-chart-line").style("font-size: 1rem;")).headline("Statistics").on(EventType.click, evt->{
            evt.preventDefault();
        }).end();
        posts.subscribe(this::layout);
    }
    private void layout(CatalogItem[] list) {
        CardContainer cast = Js.uncheckedCast(container.element());
        cast.clear();
        Card[] cards = cast.add(list);
        for(int i = 0; i < list.length; ++i) {
            var item = list[i];
            cards[i].onClick(evt->DomGlobal.console.log("/posts/" + item.id));
            HTMLElement elem = Js.uncheckedCast(cards[i]);
            elem.onclick = evt->onClickCard((MouseEvent) evt, elem, item);
        }
    }
    @JsType(isNative=true)
    private interface CardContainer {
        @JsMethod Card[] add(CatalogItem[] items);
        @JsMethod void clear();
    }
    @JsType(isNative=true)
    private interface Card {
        @JsMethod void onClick(EventCallbackFn<MouseEvent> callback);
    }
    private CatalogItem selected;
    private Object onClickCard(MouseEvent evt, HTMLElement elem, CatalogItem item) {
        evt.stopImmediatePropagation();
        menu.anchorElement(elem).open();
        var x = evt.offsetX - 20;
        var y = evt.offsetY - elem.clientHeight - 20;
        HTMLElement target = (HTMLElement) evt.target;
        if(target != elem) {
            if(x + target.offsetLeft + target.offsetWidth + 100 < elem.offsetWidth) x += target.offsetLeft;
            y += target.offsetTop;
        }
        menu.offset((int)x, (int)y);
        selected = item;
        return null;
    }
}
