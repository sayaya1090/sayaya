package net.sayaya.client.dom;

import elemental2.core.Function;
import elemental2.dom.*;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import lombok.experimental.Delegate;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import java.util.Arrays;
import java.util.function.Supplier;

import static org.jboss.elemento.Elements.*;

@JsType
public class ConsoleElement extends CustomElement {
    private Line last = null;
    private HTMLContainerBuilder<HTMLDivElement> div;
    public static void initialize(ConsoleElement instance) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        instance.div= div().id("console").css("console");
        shadowRoot.append(
                htmlElement("link", HTMLLinkElement.class).attr("rel", "stylesheet").attr("href", "css/console.css").element(),
                instance.div.element()
        );
    }
    public void type(String text, boolean center) {
        close();
        if(text.contains("\n")) {
            int index = text.indexOf("\n");
            String first = text.substring(0, index);
            String remains = text.substring(index+1);
            last = Line.type(first, ()->{
                type(remains, center);
                return null;
            });
        } else last = Line.type(text, null);
        if(center) last.css("line-center");
        div.add(last);
        scrollTop = scrollHeight;
    }
    public void print(String text, boolean center) {
        Arrays.stream(text.split("\n")).map(Line::print).forEach(line->{
            close();
            last = line;
            if(center) last.css("line-center");
            div.add(last);
        });
        scrollTop = scrollHeight;
    }
    @JsIgnore public void add(IsElement<? extends HTMLElement> elem) {
        add(elem.element());
    }
    public void add(HTMLElement elem) {
        String id = "Row_" + div.element().childElementCount;
        elem.setAttribute("slot", id);
        elem.style.width = CSSProperties.WidthUnionType.of("100%");
        var line = new Line();
        line.style("width: 100%; overflow: visible;");
        line.add(htmlElement("slot", HTMLSlotElement.class).attr("name", id));
        line.close();
        div.add(line);
        append(elem);
    }
    public void close() {
        if(last!=null) last.close();
    }
    public void clear() {
        div.element().innerHTML = "";
        innerHTML = "";
    }
    public static final class Line implements IsElement<HTMLDivElement> {
        public static Line type(String text, Supplier<Void> onComplete) {
            var line = new Line();
            var iter = text.chars().mapToObj(c -> (char) c).map(c->{
                if(c == '\n') return "<br/>";
                else return c;
            }).iterator();
            repeatUntil(args->line.add(iter.next().toString()), 50, iter::hasNext, onComplete);
            return line;
        }
        public static Line print(String text) {
            var line = new Line();
            line.element().innerHTML = text;
            return line;
        }
        @Delegate final HTMLContainerBuilder<HTMLDivElement> _this = div().css("line");
        public Line() {
            this.element().innerHTML = "";
        }
        public void close() {
            element().style.borderRight = "0px hidden";
        }
        static native Function repeatUntil(DomGlobal.SetTimeoutCallbackFn func, double step, Supplier<Boolean> check, Supplier<Void> onComplete) /*-{
            function nextTimeout(min) { return min+Math.floor(Math.random() * 50) }
            var timerId = setTimeout(function tick() {
                func();
                if(check.@java.util.function.Supplier::get()()) timerId = setTimeout(tick, nextTimeout(step));
                else if(onComplete!=null) onComplete.@java.util.function.Supplier::get()()
            }, nextTimeout(step));
            return function() {
                if(timerId) clearTimeout(timerId);
                timerId = 0;
            }
        }-*/;
    }
}