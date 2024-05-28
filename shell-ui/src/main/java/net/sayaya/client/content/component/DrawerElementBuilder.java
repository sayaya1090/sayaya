package net.sayaya.client.content.component;

import elemental2.dom.*;
import lombok.experimental.Delegate;
import net.sayaya.client.content.ContentModule;
import net.sayaya.client.data.Menu;
import net.sayaya.client.data.Page;
import net.sayaya.rx.subject.BehaviorSubject;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Boolean.TRUE;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static net.sayaya.client.content.ContentModule.MenuState.*;
import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static org.jboss.elemento.Elements.*;

@Singleton
public class DrawerElementBuilder implements IsElement<HTMLElement> {
    @Delegate private final HTMLContainerBuilder<HTMLElement> _this = nav();
    private final MenuToggleButtonElementBuilder btnToggle;
    private final NavigationRailElementBuilder navMenu = NavigationRailElementBuilder.nav();
    private final NavigationRailElementBuilder navPage = NavigationRailElementBuilder.nav();
    private Menu menuSelected = null;
    private Page pageSelected = null;
    private final BehaviorSubject<ContentModule.MenuState> state;
    private final MenuManager menu;
    private final BehaviorSubject<Page> page;
    @Inject DrawerElementBuilder(BehaviorSubject<ContentModule.MenuState> state, MenuManager menu, BehaviorSubject<Page> page, MenuToggleButtonElementBuilder btnToggle) {
        this.state = state;
        this.btnToggle = btnToggle;
        this.menu = menu;
        this.page = page;
        layout();
        state.subscribe(s->{
            if(s == SHOW) open();
            else collapse();
        });
        menu.subscribe(this::updateMenuPage);
        page.subscribe(this::selectMenuByPage);
    }
    private void layout() {
        _this.css("drawer")
                .add(btnToggle.style("margin: 8px;"))
                .add(div().style("display: flex; height: -webkit-fill-available;")
                          .add(navMenu).add(navPage));
    }

    private void updateMenuPage(List<Menu> menu) {
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
                    selectMenuByPage(item);
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
    private void selectMenuByPage(Menu menu) {
        navPage.clear();
        if(menu.children!=null) Arrays.stream(menu.children)
                .sorted(nullsLast(comparing((Page i) -> i.order)))
                .forEach(item-> toPageItem(menu, item));
        addBackToMenuRail();
    }
    private void selectMenuByPage(Page page) {
        if(pageSelected == page) return;
        var parent = menu.parentOf(page);
        select(parent, page);
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
        page.next(p);
        updateMenuPage(menu.getValue());
        selectMenuByPage(m);
    }
    private void open() {
        navMenu.expand();
        if(menuSelected == null || menuSelected.children==null || menuSelected.children.length <= 1) navPage.hide();
        else navPage.expand();
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
}