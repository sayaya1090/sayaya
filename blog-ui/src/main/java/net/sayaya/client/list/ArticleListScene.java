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
import org.jboss.elemento.EventCallbackFn;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

import static org.jboss.elemento.Elements.div;

@Singleton
public class ArticleListScene extends HTMLContainerBuilder<HTMLDivElement> {
    private final HTMLContainerBuilder<HTMLElement> container;
    @Inject ArticleListScene(HTMLContainerBuilder<HTMLElement> container, BehaviorSubject<CatalogItem[]> articles, BehaviorSubject<Set<String>> params) {
        super(div().element());
        this.css("compilation")
                /*.add(searchBoxContainer.css("search-box-container")
                        .add(iptSearchBox)
                        .add(chips))*/
                .add(container);
        this.container = container;
        articles.subscribe(this::layout);
        /*
        window.addEventListener(EventType.wheel.name, evt->{
            var cast = (WheelEvent)evt;
            if(cast.deltaY > 0 ) isSearchMode.next(true);
            else if(cast.deltaY < 0 && container.element().scrollTop+cast.deltaY<=0 ) isSearchMode.next(false);
        });
        isSearchMode.subscribe(mode->{
            if(mode) searchBoxContainer.element().style.marginTop = CSSProperties.MarginTopUnionType.of("0vh");
            else searchBoxContainer.element().style.marginTop = CSSProperties.MarginTopUnionType.of("32vh");
        });
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
            mtxInitialize.set(true);
            chips.element().innerHTML = "";
            for(var t: tag) {
                var chip = chips.input().label(t);
                chip.element().addEventListener("DOMNodeRemoved", evt->{
                    if(mtxInitialize.get()) return;
                    params.getValue().remove(t);
                    params.next(params.getValue());
                });
            }
            mtxInitialize.set(false);
        });*/
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
