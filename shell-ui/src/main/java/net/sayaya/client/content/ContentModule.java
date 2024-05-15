package net.sayaya.client.content;

import dagger.Provides;
import elemental2.dom.DomGlobal;
import net.sayaya.client.content.dom.*;
import net.sayaya.client.dom.CustomElements;
import net.sayaya.rx.subject.BehaviorSubject;

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
    public static void defineCustomTags() {
        CustomElements.define("sac-menu-button", MenuToggleButtonElement.class, MenuToggleButtonElement::initialize);
        CustomElements.define("sac-navigation-rail", NavigationRailElement.class, NavigationRailElement::initialize);
        CustomElements.define("sac-navigation-rail-item", NavigationRailItemElement.class, NavigationRailItemElement::initialize);
        CustomElements.define("sac-progress", ProgressElement.class, ProgressElement::initialize);
        // CustomElements.define("sac-shell", ShellElement.class, ShellElement::initialize);
    }
    public enum MenuState {
        SHOW, HIDE
    }
}
