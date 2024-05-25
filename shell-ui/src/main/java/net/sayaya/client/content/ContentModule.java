package net.sayaya.client.content;

import dagger.Provides;
import net.sayaya.client.content.dom.*;
import net.sayaya.client.data.Page;
import net.sayaya.client.dom.CustomElements;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class ContentModule {
    @Provides @Singleton static BehaviorSubject<Progress> provideProgress() {
        Progress impl = new Progress();
        BehaviorSubject<Progress> subject = behavior(impl);
        impl.onValueChange(subject);
        return subject;
    }
    @Provides @Singleton static BehaviorSubject<MenuState> provideMenuState() {
        return behavior(MenuState.HIDE);
    }
    @Provides @Singleton static BehaviorSubject<Page> providePage(@Named("url") BehaviorSubject<String> url) {
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
