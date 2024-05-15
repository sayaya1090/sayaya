package net.sayaya.client.dom;

import com.google.gwt.core.client.Scheduler;
import elemental2.dom.*;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.sayaya.client.api.OAuthApi;
import net.sayaya.ui.elements.ButtonElementBuilder;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static org.jboss.elemento.Elements.*;
import static org.jboss.elemento.Elements.htmlElement;

@JsType
public class LoginElement extends CustomElement {
    private HTMLContainerBuilder<HTMLDivElement> div;
    private ConsoleElement console;
    @Setter(onMethod_ = { @JsIgnore }) @Accessors(fluent = true)
    private OAuthApi api;
    private ButtonElementBuilder.TextButtonElementBuilder btnLoginGithub;
    private ButtonElementBuilder.TextButtonElementBuilder btnLoginGoogle;
    private ButtonElementBuilder.TextButtonElementBuilder[] selects;
    private HTMLAudioElement beep;
    private HTMLAudioElement start;
    private final static String WELCOME =
            " ___  ___  _ _  ___  _ _  ___     _ _  ___  ___ \n" +
            "/ __>| . || | || . || | || . |   | \\ || __>|_ _|\n" +
            "\\__ \\|   |\\   /|   |\\   /|   | _ |   || _>  | | \n" +
            "<___/|_|_| |_| |_|_| |_| |_|_|<_>|_\\_||___> |_| \n" +
            "================================================\n" +
            " :: Web ::                             (v0.1.0) \n" +
            " \n" +
            " \n";
    private ButtonElementBuilder.TextButtonElementBuilder cursor = null;
    public static void initialize(LoginElement instance) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        instance.div= div().id("console").css("console");
        shadowRoot.append(
                script().attr("type", "text/javascript").attr("src", "js/fontawesome.min.js").element(),
                htmlElement("link", HTMLLinkElement.class).attr("rel", "stylesheet").attr("href", "css/fontawesome.min.css").element(),
                script().attr("type", "text/javascript").attr("src", "js/brands.min.js").element(),
                htmlElement("link", HTMLLinkElement.class).attr("rel", "stylesheet").attr("href", "css/brands.min.css").element(),
                instance.div.style("height: 100%; display: flex;").add(htmlElement("slot", HTMLSlotElement.class)).element()
        );
        instance.btnLoginGithub = button().text().css("button").add("GITHUB").icon(icon().css("fa-brands", "fa-github"));
        instance.btnLoginGoogle = button().text().css("button").add("GOOGLE").icon(icon().css("fa-brands", "fa-google"));
        instance.beep = audio().attr("src", "wav/beep.mp3").element();
        instance.start = audio().attr("src", "wav/start.mp3").element();
    }
    public LoginElement console(ConsoleElement console) {
        this.console = console;
        this.append(console);
        return this;
    }
    public LoginElement attached() {
        selects = new ButtonElementBuilder.TextButtonElementBuilder[]{ btnLoginGithub, btnLoginGoogle };
        addEventListener(EventType.click.name, evt->cursor.element().focus());
        Scheduler.get().scheduleDeferred(() -> {
            console.style.height = CSSProperties.HeightUnionType.of("22.5rem");
            btnLoginGithub.onClick(evt -> login("github"));
            btnLoginGoogle.onClick(evt -> login("google"));
        });
        console.type(" \nWelcome to\n ", false);
        DomGlobal.setTimeout(arg2 -> {
            console.print(WELCOME, true);
            console.print("> SELECT YOUR AUTHENTICATION PROVIDER:\n ", false);
            console.close();
            console.add(btnLoginGithub);
            console.add(btnLoginGoogle);
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
        return this;
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
        console.style.height = CSSProperties.HeightUnionType.of("36rem");
        console.style.transitionDuration = "100ms";
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
}
