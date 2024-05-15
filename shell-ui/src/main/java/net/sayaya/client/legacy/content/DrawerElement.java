package net.sayaya.client.legacy.content;

import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import net.sayaya.client.data.Menu;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.elements.ListElementBuilder;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Boolean.TRUE;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static net.sayaya.ui.elements.ListElementBuilder.list;
import static org.jboss.elemento.Elements.*;

@Singleton
public class DrawerElement extends HTMLContainerBuilder<HTMLElement> implements IsElement<HTMLElement> {
    @Inject DrawerElement(NavigationRailElement navigation, @Named("url") BehaviorSubject<String> contentUrl, MenuManager menuManager) {
        this(nav(), navigation, contentUrl, menuManager);
    }
    private final HTMLContainerBuilder<HTMLElement> _this;
    private final MenuButton btnToggleMenu;
    private final ListElementBuilder list;
    private DrawerElement(HTMLContainerBuilder<HTMLElement> element, NavigationRailElement navigation, BehaviorSubject<String> contentUrl, MenuManager menuManager) {
        super(element.element());
        _this = element;
        btnToggleMenu = MenuButton.menu(menuManager);
        list = list();
        list.element().classList.add("menu");
        layout(navigation);
        btnToggleMenu.onClick(evt->menuManager.toggle());
        menuManager.onUpdate(menu->update(menuManager, menu, contentUrl));
        menuManager.onStateChange(state->{
            switch(state) {
                case SHOW:
                    menuManager.reload();
                    _this.element().setAttribute("open", "");
                    break;
                case HIDE:
                    update(menuManager, menuManager.menu(), contentUrl);
                    _this.element().removeAttribute("open");
                    break;
            }
        });
        contentUrl.subscribe(evt->update(menuManager, menuManager.menu(), contentUrl));
    }
    private void update(MenuManager menuManager, List<Menu> menu, BehaviorSubject<String> contentUrl) {
        list.element().innerHTML = "";
        AtomicBoolean bottom = new AtomicBoolean(false);
        menu.stream().sorted(nullsLast(comparing((Menu i) -> TRUE.equals(i.bottom)).thenComparing(i->i.order))).forEach(item-> {
            var child = appendItem(item, menuManager, contentUrl);
            if(TRUE.equals(item.bottom) && !bottom.getAndSet(true)) child.element().style.marginTop = CSSProperties.MarginTopUnionType.of("auto");
        });
    }
    private ListElementBuilder.ListItemElementBuilder appendItem(Menu item, MenuManager menuManager, BehaviorSubject<String> contentUrl) {
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
            if(urlAndHash!=null && urlAndHash.contains("#")) {
                var url = urlAndHash.split("#")[0];
                if (Arrays.stream(item.children).anyMatch(i -> equalUri(i.uri, url))) {
                    child.element().setAttribute("selected", "");
                    menuManager.pages(item.children);
                }
            }
            child.on(EventType.click, evt->contentUrl.next(item.children[0].uri));
        }
        child.on(EventType.click, evt->menuManager.state(MenuManager.MenuState.HIDE));
        child.on(EventType.mouseover, evt->menuManager.pages(item.children));
        return child;
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
