package net.sayaya.client.legacy.content;

import net.sayaya.client.api.ShellApi;
import net.sayaya.client.data.Menu;
import net.sayaya.client.data.Page;
import net.sayaya.rx.Observer;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.Subject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.List;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static net.sayaya.rx.subject.Subject.subject;

@Singleton
public class MenuManager {
    private final BehaviorSubject<List<Menu>> menu = behavior(new LinkedList<>());
    private final BehaviorSubject<MenuState> state = behavior(MenuState.HIDE);
    private final Subject<Page[]> pages = subject(Page[].class);
    private final ShellApi shellApi;
    @Inject MenuManager(ShellApi shellApi) {
        this.shellApi = shellApi;
        reload();
    }
    void reload() {
        shellApi.findMenu().then(m -> {
            menu.next(m);
            return null;
        });
    }
    void onUpdate(OnUpdateObserver observer) {
        menu.subscribe(observer);
    }
    List<Menu> menu() { return menu.getValue(); }
    void onPageUpdate(OnPageUpdateObserver observer) {
        pages.subscribe(observer);
    }
    void pages(Page[] next) { pages.next(next); }
    void onStateChange(OnStateChangeObserver observer) {
        state.subscribe(observer);
    }
    void state(MenuState next) { state.next(next); }
    public MenuState state() { return state.getValue(); }
    void toggle() {
        state(state()== MenuManager.MenuState.SHOW ? MenuManager.MenuState.HIDE : MenuManager.MenuState.SHOW );
    }
    public enum MenuState {
        SHOW, HIDE
    }
    interface OnUpdateObserver extends Observer.ObserverDefault<List<Menu>> {
        @Override void next(List<Menu> param);
    }
    interface OnPageUpdateObserver extends Observer.ObserverDefault<Page[]> {
        @Override void next(Page[] param);
    }
    interface OnStateChangeObserver extends Observer.ObserverDefault<MenuState> {
        @Override void next(MenuState param);
    }
}
