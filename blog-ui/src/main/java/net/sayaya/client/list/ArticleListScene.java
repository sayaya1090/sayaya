package net.sayaya.client.list;

import elemental2.dom.*;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.elements.ChipsElementBuilder;
import net.sayaya.ui.elements.TextFieldElementBuilder;
import org.jboss.elemento.EventCallbackFn;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

import static elemental2.dom.DomGlobal.window;
import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static net.sayaya.ui.elements.TextFieldElementBuilder.textField;
import static org.jboss.elemento.Elements.div;

@Singleton
public class ArticleListScene extends HTMLContainerBuilder<HTMLDivElement> {
    private final HTMLContainerBuilder<HTMLDivElement> searchBoxContainer = div();
    private final TextFieldElementBuilder.OutlinedTextFieldElementBuilder iptSearchBox = textField().outlined().label("Search");
    private final ChipsElementBuilder chips = ChipsElementBuilder.chips();
    private final HTMLContainerBuilder<HTMLElement> container;
    @Inject ArticleListScene(HTMLContainerBuilder<HTMLElement> container, BehaviorSubject<CatalogItem[]> articles, BehaviorSubject<Set<String>> params) {
        super(div().element());
        this.css("article-list")
                .add(searchBoxContainer.css("search-box-container")
                        .add(iptSearchBox)
                        .add(chips))
                .add(container.css("card-container"));
        this.container = container;
        articles.subscribe(this::layout);
        iptSearchBox.onChange(evt->{
            var value = iptSearchBox.value();
            if(value==null || value.length() < 3) return;
            var set = params.getValue();
            if(set==null) set = new HashSet<>();
            set.add(value);
            params.next(set);
            iptSearchBox.value("");
        });
        params.subscribe(tag->{
            if(tag==null) return;
            chips.element().innerHTML = "";
            for(var t: tag) {
                var chip = chips.input().label(t);
                chip.element().addEventListener("DOMNodeRemoved", evt->{
                    params.getValue().remove(t);
                    params.next(params.getValue());
                });
            }
        });
        BehaviorSubject<Boolean> isSearchMode = behavior(false);
        window.addEventListener(EventType.wheel.name, evt->{
            var cast = (WheelEvent)evt;
            if(cast.deltaY > 0 ) isSearchMode.next(true);
            else if(cast.deltaY < 0 && container.element().scrollTop+cast.deltaY<=0 ) isSearchMode.next(false);
        });
        isSearchMode.subscribe(mode->{
            if(mode) searchBoxContainer.element().style.marginTop = CSSProperties.MarginTopUnionType.of("0vh");
            else searchBoxContainer.element().style.marginTop = CSSProperties.MarginTopUnionType.of("32vh");
        });
    }
    private void layout(CatalogItem[] list) {
        CardContainer cast = Js.uncheckedCast(container.element());
        cast.clear();
        Card[] cards = cast.add(list);
        for(int i = 0; i < list.length; ++i) {
            var item = list[i];
            cards[i].onClick(evt-> DomGlobal.console.log("/posts/" + item.id));
            HTMLElement elem = Js.uncheckedCast(cards[i]);
            elem.onclick = evt->onClickCard((MouseEvent) evt, elem, item);
        }
    }
    @JsType(isNative=true)
    private interface CardContainer {
        @JsMethod
        Card[] add(CatalogItem[] items);
        @JsMethod void clear();
    }
    @JsType(isNative=true)
    private interface Card {
        @JsMethod void onClick(EventCallbackFn<MouseEvent> callback);
    }
    private Object onClickCard(MouseEvent evt, HTMLElement elem, CatalogItem item) {
        evt.stopImmediatePropagation();
        DomGlobal.console.log(item);
        return null;
    }
}
