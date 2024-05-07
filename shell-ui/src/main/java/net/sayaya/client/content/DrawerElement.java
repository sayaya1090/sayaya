package net.sayaya.client.content;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import net.sayaya.client.api.ShellApi;
import net.sayaya.client.data.Menu;
import net.sayaya.client.data.Page;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.Subject;
import net.sayaya.ui.elements.IconButtonElementBuilder;
import net.sayaya.ui.elements.ListElementBuilder;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static net.sayaya.ui.elements.ListElementBuilder.list;
import static org.jboss.elemento.Elements.*;

@Singleton
public class DrawerElement extends HTMLContainerBuilder<HTMLElement> implements IsElement<HTMLElement> {
    @Inject DrawerElement(NavigationRailElement navigation, @Named("contentUrl") BehaviorSubject<String> contentUrl, @Named("isMenuShown") BehaviorSubject<Boolean> isMenuShown, Subject<Page[]> pages) {
        this(nav(), navigation, contentUrl, isMenuShown, pages);
    }
    private final HTMLContainerBuilder<HTMLElement> _this;
    private final IconButtonElementBuilder.PlainIconButtonElementBuilder btnToggleMenu = button().icon().add(icon("menu")).ariaLabel("Menu");
    private final ListElementBuilder list;
    private final BehaviorSubject<List<Menu>> menu = behavior(new LinkedList<>());
    private DrawerElement(HTMLContainerBuilder<HTMLElement> element, NavigationRailElement navigation, BehaviorSubject<String> contentUrl, BehaviorSubject<Boolean> isMenuShown, Subject<Page[]> pages) {
        super(element.element());
        _this = element;
        list = list();
        list.element().classList.add("menu");
        layout(navigation);
        btnToggleMenu.onClick(evt->isMenuShown.next(!isMenuShown.getValue()));
        isMenuShown.subscribe(show -> {
            if(show) {
                ShellApi.findMenu().then(menu -> {
                    this.menu.next(menu);
                    return null;
                });
                _this.element().setAttribute("open", "");
            } else {
                update(isMenuShown, menu.getValue(), contentUrl, pages);
                _this.element().removeAttribute("open");
            }
        });
        ShellApi.findMenu().then(m -> {
            menu.subscribe(menu->update(isMenuShown, menu, contentUrl, pages));
            this.menu.next(m);
            return null;
        });
        contentUrl.subscribe(evt->update(isMenuShown, menu.getValue(), contentUrl, pages));
    }
    private void update(BehaviorSubject<Boolean> isMenuShown, List<Menu> menu, BehaviorSubject<String> contentUrl, Subject<Page[]> pages) {
        list.element().innerHTML = "";
        menu.stream().sorted(nullsLast(comparing(i->i.order))).forEach(item->appendItem(item, isMenuShown, contentUrl, pages));
    }
    private void appendItem(Menu item, BehaviorSubject<Boolean> isMenuShown, BehaviorSubject<String> contentUrl, Subject<Page[]> pages) {
        var child = list.item().css("item").type("button");
        if(item.title!=null) child.headline(item.title);
        if(item.icon!=null) {
            if(item.icon.startsWith("fa-")) child.start(i().css("fa-sharp", "fa-light", item.icon).element());
            else child.start(icon(item.icon));
        }
        if(item.supportingText!=null) child.supportingText(item.supportingText);
        if(item.trailingText!=null) child.trailingSupportingText(item.trailingText);
        if(item.children!=null && item.children.length > 0) {
            var urlAndHash = contentUrl.getValue();
            var url = urlAndHash.split("#")[0];
            if(Arrays.stream(item.children).anyMatch(i->equalUri(i.uri, url))) {
                child.element().setAttribute("selected", "");
                pages.next(item.children);
            }
            child.on(EventType.click, evt->contentUrl.next(item.children[0].uri));
        }
        child.on(EventType.click, evt->isMenuShown.next(false));
        child.on(EventType.mouseover, evt->pages.next(item.children));
    }
    private final static String HOST = DomGlobal.window.location.protocol + "//" + DomGlobal.window.location.host;
    static boolean equalUri(String uri1,String uri2) {
        if(uri1==null && uri2==null) return true;
        if(uri1==null || uri2==null) return false;
        if(uri1.startsWith("/")) uri1 = HOST + uri1;
        if(uri2.startsWith("/")) uri2 = HOST + uri2;
        return uri1.equals(uri2);
    }
    private void layout(NavigationRailElement navigation) {
        _this.css("drawer")
             .add(btnToggleMenu)
             .add(div().style("display: flex; height: -webkit-fill-available;")
                 .add(list)
                 .add(navigation));
    }
    @Override public DrawerElement that() {
        return this;
    }
}
