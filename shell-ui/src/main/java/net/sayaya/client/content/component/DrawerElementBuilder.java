package net.sayaya.client.content.component;

import elemental2.dom.CSSProperties;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import lombok.experimental.Delegate;
import net.sayaya.client.api.ShellApi;
import net.sayaya.client.content.ContentModule;
import net.sayaya.client.data.Menu;
import net.sayaya.client.data.Page;
import net.sayaya.rx.subject.BehaviorSubject;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Boolean.TRUE;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static net.sayaya.client.content.ContentModule.MenuState.*;
import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static org.jboss.elemento.Elements.*;

@Singleton
public class DrawerElementBuilder implements IsElement<HTMLElement> {
    @Delegate private final HTMLContainerBuilder<HTMLElement> _this = nav();
    private final MenuToggleButtonElementBuilder btnToggle;
    private final NavigationRailElementBuilder navMenu = NavigationRailElementBuilder.nav();
    private final NavigationRailElementBuilder navPage = NavigationRailElementBuilder.nav();
    private final BehaviorSubject<List<Menu>> menu = behavior(new LinkedList<>());
    private final ShellApi shellApi;
    private Menu menuSelected = null;
    private Page pageSelected = null;
    private final BehaviorSubject<ContentModule.MenuState> state;
    private final FrameElementBuilder frame;
    private final BehaviorSubject<String> url;
    @Inject DrawerElementBuilder(ShellApi shellApi, BehaviorSubject<ContentModule.MenuState> state, MenuToggleButtonElementBuilder btnToggle, FrameElementBuilder frame, @Named("url") BehaviorSubject<String> url) {
        this.shellApi = shellApi;
        this.state = state;
        this.btnToggle = btnToggle;
        this.frame = frame;
        this.url = url;
        layout();
        state.subscribe(s->{
            if(s == SHOW) open();
            else collapse();
        });
        menu.subscribe(this::update);
        url.subscribe(this::update);
        reload();
    }
    private void layout() {
        _this.css("drawer")
                .add(btnToggle.style("margin: 8px;"))
                .add(div().style("display: flex; height: -webkit-fill-available;")
                          .add(navMenu).add(navPage));
    }
    private void reload() {
        shellApi.findMenu().then(m -> {
            menu.next(m);
            return null;
        });
    }
    private void update(List<Menu> menu) {
        navMenu.clear();
        AtomicBoolean bottom = new AtomicBoolean(false);
        menu.stream().sorted(nullsLast(comparing((Menu i) -> TRUE.equals(i.bottom)).thenComparing(i->i.order))).forEach(item-> {
            var child = toMenuItem(item);
            if(TRUE.equals(item.bottom) && !bottom.getAndSet(true)) child.element().style.marginTop = CSSProperties.MarginTopUnionType.of("auto");
        });
    }
    private NavigationRailItemElementBuilder toMenuItem(Menu item) {
        var child = navMenu.item();
        child.icon(icon().css("fa-sharp", "fa-light", item.icon))
                .start(icon().css("fa-sharp", "fa-light", item.icon))
                .headline(item.title).select(item==menuSelected);
        if(item.supportingText!=null) child.supportingText(item.supportingText);
        if(item.children!=null && item.children.length > 1) {
            child.trailingSupportingText("▶");
            child.on(EventType.mouseover, evt-> {
                if(state.getValue() == SHOW) {
                    update(item);
                    navPage.element().style.paddingTop = CSSProperties.PaddingTopUnionType.of((child.element().offsetTop - navMenu.element().offsetTop) + "px");
                    navPage.expand();
                }
            });
        } else child.on(EventType.mouseover, evt-> {
            navPage.clear();
            navPage.hide();
        });
        child.on(EventType.click, evt->select(item));
        return child;
    }
    private void update(Menu menu) {
        navPage.clear();
        if(menu.children!=null) Arrays.stream(menu.children)
                .sorted(nullsLast(comparing((Page i) -> i.order)))
                .forEach(item-> toPageItem(menu, item));
        addBackToMenuRail();
    }
    private void toPageItem(Menu menu, Page item) {
        var child = navPage.item();
        child.icon(icon().css("fa-sharp", "fa-light", item.icon))
                .start(icon().css("fa-sharp", "fa-light", item.icon))
                .headline(item.title).select(item==pageSelected);
        child.on(EventType.click, evt->select(menu, item));
    }
    private void addBackToMenuRail() {
        var child = navPage.item();
        child.element().style.marginTop = CSSProperties.MarginTopUnionType.of("auto");
        child.icon(icon().css("fa-sharp", "fa-light", "fa-left"));
        child.on(EventType.click, evt->{
            evt.preventDefault();
            navMenu.collapse();
            navPage.hide();
        });
    }
    private void select(Menu m) {
       if(m.children !=null && m.children.length > 0) select(m, m.children[0]);
       else select(m, null);
    }
    private void select(Menu m, Page p) {
        menuSelected = m;
        pageSelected = p;
        collapse();
        state.next(HIDE);
        frame.element().innerHTML = "";
        if(p!=null && p.tag!=null) {
            try {
                var elem = createHtmlElement(p.tag, HTMLElement.class);
                frame.element().appendChild(elem);
                show(elem, null);
            } catch(Exception e) {
                e.printStackTrace();
            }
            url.next(p.uri);
        }
        update(menu.getValue());
        update(m);
    }
    public native void show(Element elem, String param) /*-{
        elem.prepare(param);
        elem.draw();
    }-*/;
    private void open() {
        if(menuSelected == null || menuSelected.children==null || menuSelected.children.length <= 1) {
            navMenu.expand();
            navPage.hide();
        } else {
            navMenu.expand();
            navPage.expand();
        }
        element().setAttribute("open", true);
    }
    private void collapse() {
        if(menuSelected == null) {
            navMenu.hide();
            navPage.hide();
        } else if(menuSelected.children==null || menuSelected.children.length <= 1) {
            navMenu.collapse();
            navPage.hide();
        } else {
            navMenu.hide();
            navPage.collapse();
        }
        element().removeAttribute("open");
    }
    private void update(String url) {
        menu.getValue().forEach(m->{
            var opt = Arrays.stream(m.children).filter(p->url.startsWith(p.uri)).max(Comparator.comparingInt(p->p.uri.length()));
            if(opt.isEmpty()) return;
            var p = opt.get();
            if(pageSelected == p) return;
            select(m, p);
        });
    }
}