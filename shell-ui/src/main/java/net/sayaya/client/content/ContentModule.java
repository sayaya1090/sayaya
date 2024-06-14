package net.sayaya.client.content;

import dagger.Provides;
import jsinterop.annotations.JsFunction;
import jsinterop.base.Js;
import net.sayaya.client.content.dom.*;
import net.sayaya.client.data.Page;
import net.sayaya.client.data.Progress;
import net.sayaya.client.dom.CustomElements;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.BehaviorSubjectJs;
import net.sayaya.client.content.component.ProgressElementBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class ContentModule {
    @Provides @Singleton static Progress provideProgress(ProgressElementBuilder elem) {
        return proxy(new Progress().enabled(false).intermediate(false).max(0.0).value(0.0), (t, p, v, r) -> {
            Js.asPropertyMap(t).set(p, v);
            elem.update(t);
            return true;
        });
    }
    private static native <T> T proxy(T any, JsSetter<T> callback) /*-{
        return new Proxy(any, {
            set: callback
        });
    }-*/;
    @JsFunction interface JsSetter<T> {
        boolean call(T target, String property, Object value, Object receiver);
    }
    @Provides @Singleton static BehaviorSubject<MenuState> provideMenuState() {
        return behavior(MenuState.HIDE);
    }
    @Provides @Singleton static BehaviorSubject<Page> providePage(@Named("url") BehaviorSubjectJs<String> url) {
        BehaviorSubject<Page> behavior = behavior(null);
        behavior.subscribe(page->{
            if(page!=null) url.next(page.uri);
            else url.next(null);
        });
        return behavior;
    }
    public static void defineCustomTags() {
        CustomElements.define("sac-menu-button", MenuToggleButtonElement.class, MenuToggleButtonElement::initialize);
        CustomElements.define("sac-navigation-rail", NavigationRailElement.class, NavigationRailElement::initialize);
        CustomElements.define("sac-navigation-rail-item", NavigationRailItemElement.class, NavigationRailItemElement::initialize);
        CustomElements.define("sac-progress", ProgressElement.class, ProgressElement::initialize);
    }
    public enum MenuState {
        SHOW, HIDE
    }
}
