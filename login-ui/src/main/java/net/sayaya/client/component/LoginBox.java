package net.sayaya.client.component;

import com.google.gwt.core.client.Scheduler;
import elemental2.dom.*;
import net.sayaya.client.api.OAuthApi;
import net.sayaya.ui.elements.ButtonElementBuilder;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Arrays;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static org.jboss.elemento.Elements.*;

@Singleton
public class LoginBox implements IsElement<HTMLDivElement> {
    private final HTMLContainerBuilder<HTMLDivElement> _this;
    private final HTMLContainerBuilder<HTMLFormElement> contents = form().css("content");
    private final ButtonElementBuilder.TextButtonElementBuilder btnLoginGithub = button().text().css("button").add("GITHUB").icon(icon("github").css("fa-brands"));
    private final ButtonElementBuilder.TextButtonElementBuilder btnLoginGoogle = button().text().css("button").add("GOOGLE").icon(icon("google").css("fa-brands"));
    private final ButtonElementBuilder.TextButtonElementBuilder[] selects = new ButtonElementBuilder.TextButtonElementBuilder[]{
            btnLoginGithub, btnLoginGoogle
    };
    private ButtonElementBuilder.TextButtonElementBuilder cursor = null;
    private final OAuthApi api;
    private final HTMLAudioElement beep = audio().attr("src", "wav/beep.mp3").element();
    private final HTMLAudioElement start = audio().attr("src", "wav/start.mp3").element();
    private final static String WELCOME =
            " ___  ___  _ _  ___  _ _  ___     _ _  ___  ___ \n" +
            "/ __>| . || | || . || | || . |   | \\ || __>|_ _|\n" +
            "\\__ \\|   |\\   /|   |\\   /|   | _ |   || _>  | | \n" +
            "<___/|_|_| |_| |_|_| |_| |_|_|<_>|_\\_||___> |_| \n" +
            "================================================\n" +
            " :: Web ::                             (v0.1.0) \n" +
            " \n" +
            " \n";
    private final Console console;
    @Inject LoginBox(Console console, OAuthApi oauth) {
        _this = div().css("box").style("height: 100%; display: flex;")
                .add(console);
        this.console = console;
        this.api = oauth;
        Scheduler.get().scheduleDeferred(() -> console.element().style.height = CSSProperties.HeightUnionType.of("22.5rem"));
        console.type(" \nWelcome to\n ");
        DomGlobal.setTimeout(arg2 -> {
            console.print(WELCOME, true);
            console.print("> SELECT YOUR AUTHENTICATION PROVIDER:");
            console.close();
            console.add(btnLoginGithub).add(btnLoginGoogle);
            for (int i = 0; i < selects.length; ++i) {
                var idx = i;
                var UP = "ArrowUp";
                var DOWN = "ArrowDown";
                selects[i].on(EventType.keydown, evt -> {
                    var key = evt.key;
                    if(key.equals(UP) || key.equals(DOWN)) {
                        var next = idx + (key.equals(DOWN) ? 1 : -1);
                        if (next >= 0 && next < selects.length) {
                            select(selects[next]);
                            replay(beep);
                        }
                    }
                });
                selects[i].onClick(evt->{       // 마우스로 클릭 시 커서 이동
                    select(selects[idx]);
                    replay(start);
                });
            }
            Scheduler.get().scheduleDeferred(() -> select(selects[0]));
        }, 1000);
        _this.on(EventType.click, evt -> cursor.element().focus());
        btnLoginGithub.onClick(evt -> login("github"));
        btnLoginGoogle.onClick(evt -> login("google"));
    }
    private void select(ButtonElementBuilder.TextButtonElementBuilder item) {
        cursor = item;
        cursor.element().focus();
    }
    private void replay(HTMLAudioElement audio) {
        audio.pause();
        audio.currentTime = 0;
        audio.play();
    }
    private void login(String provider) {
        console.element().style.height = CSSProperties.HeightUnionType.of("36rem");
        console.element().style.transitionDuration = "100ms";
        clear();
        for(var i: selects) i.element().disabled = true;
        cursor.attr("selected", true);
        api.login(provider).catch_(e->{
            for(var i: selects) i.element().disabled = false;
            cursor.element().removeAttribute("selected");
            Scheduler.get().scheduleDeferred(() -> select(cursor));
            return null;
        });
    }
    private void clear() {
        var lastButton = selects[selects.length-1];
        while(lastButton.element().nextElementSibling!=null) lastButton.element().nextElementSibling.remove();
    }
    @Override
    public HTMLDivElement element() {
        return _this.element();
    }
}