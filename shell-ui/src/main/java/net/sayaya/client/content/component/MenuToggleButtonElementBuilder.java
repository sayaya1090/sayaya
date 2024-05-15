package net.sayaya.client.content.component;

import lombok.experimental.Delegate;
import net.sayaya.client.content.ContentModule;
import net.sayaya.client.content.ContentModule.MenuState;
import net.sayaya.client.content.dom.MenuToggleButtonElement;
import net.sayaya.client.dom.ContainerBuilder;
import net.sayaya.rx.subject.BehaviorSubject;
import org.jboss.elemento.EventType;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import static net.sayaya.client.content.ContentModule.MenuState.*;
import static net.sayaya.client.dom.CustomElements.customContainer;

@Singleton
public class MenuToggleButtonElementBuilder implements IsElement<MenuToggleButtonElement> {
    @Delegate private final ContainerBuilder<MenuToggleButtonElement> _this = customContainer("sac-menu-button", MenuToggleButtonElement.class);
    @Delegate private final MenuToggleButtonElement element = _this.element();
    @Inject public MenuToggleButtonElementBuilder(BehaviorSubject<ContentModule.MenuState> state) {
        on(EventType.click, evt->{
            if(state.getValue() == SHOW) state.next(HIDE);
            else state.next(SHOW);
        });
        state.subscribe(s->{
            switch(s){
                case SHOW : open(); break;
                case HIDE : close(); break;
            }
        });
    }
}
