package net.sayaya.client.content;

import elemental2.dom.HTMLElement;
import net.sayaya.client.data.Page;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.Subject;
import net.sayaya.ui.elements.ButtonElementBuilder;
import net.sayaya.ui.elements.IconButtonElementBuilder;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static net.sayaya.ui.elements.ListElementBuilder.list;
import static org.jboss.elemento.Elements.*;

@Singleton
public class NavigationRailElement extends HTMLContainerBuilder<HTMLElement> implements IsElement<HTMLElement> {
    @Inject NavigationRailElement(Subject<Page[]> pages, @Named("contentUrl") BehaviorSubject<String> contentUrl, @Named("isMenuShown") BehaviorSubject<Boolean> isMenuShown) {
        this(nav(), pages, contentUrl, isMenuShown);
    }
    private final HTMLContainerBuilder<HTMLElement> _this;
    private final BehaviorSubject<String> contentUrl;
    private final BehaviorSubject<Boolean> isMenuShown;
    private NavigationRailElement(HTMLContainerBuilder<HTMLElement> element, Subject<Page[]> pages, BehaviorSubject<String> contentUrl, BehaviorSubject<Boolean> isMenuShown) {
        super(element.css("rail").element());
        _this = element;
        this.contentUrl = contentUrl;
        this.isMenuShown = isMenuShown;
        pages.subscribe(this::update);
        isMenuShown.subscribe(show->{
            if(current==null || current.length<=1) return;
            if(show) expand();
            else collapse();
        });
    }
    private Page[] current;
    private void update(Page[] pages) {
        if(current!=null && Arrays.equals(current, pages)) return;
        else current = pages!=null? Arrays.stream(pages).sorted(nullsLast(comparing(i->i.order))).toArray(Page[]::new) : null;
        if(current==null || current.length<=1) hide();
        else show();
    }
    private void hide() {
        _this.element().innerHTML = "";
        _this.element().removeAttribute("open");
    }
    private void show() {
        _this.element().setAttribute("open", "");
        if(isMenuShown.getValue()) expand();
        else collapse();
    }
    private void expand() {
        _this.element().innerHTML = "";
        if(current!=null) {
            var list = list().style("width: 100%; background: transparent;");
            _this.add(list);
            Arrays.stream(current).forEach(item->{
                var child = list.item().css("item").type("button").headline(item.title);
                contentUrl.subscribe(url->{
                    if(item.uri!=null && DrawerElement.equalUri(item.uri, url)) child.element().setAttribute("selected", "");
                    else child.element().removeAttribute("selected");
                });
                if(item.icon!=null) {
                    if(item.icon.startsWith("fa-")) child.start(i().css("fa-sharp", "fa-light", item.icon).element());
                    else child.start(icon(item.icon));
                }
                child.on(EventType.click, evt->{
                    contentUrl.next(item.uri);
                    isMenuShown.next(false);
                });
            });
        }
    }
    private void collapse() {
        _this.element().innerHTML = "";
        if(current!=null) {
            var div = div().style("margin-top: var(--md-sys-typescale-body-medium-line-height, 1.5rem);");
            _this.add(div);
            Arrays.stream(current).forEach(item->{
                var btn = span().css("item");
                HTMLElement ico = item.icon.startsWith("fa-")? i().css("fa-sharp", "fa-light", item.icon).element():  null;
                contentUrl.subscribe(url->{
                    btn.element().innerHTML = "";
                    IconButtonElementBuilder<?, ?> icon = ButtonElementBuilder.button().icon().css("item");
                    if(DrawerElement.equalUri(item.uri, contentUrl.getValue())) icon = ((IconButtonElementBuilder.PlainIconButtonElementBuilder)icon).filled();
                    icon.add(ico);
                    btn.add(icon);
                });
                div.add(btn);
                btn.on(EventType.click, evt->contentUrl.next(item.uri));
            });
        }
    }
    @Override public NavigationRailElement that() {
        return this;
    }
}