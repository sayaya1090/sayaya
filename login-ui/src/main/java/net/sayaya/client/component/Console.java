package net.sayaya.client.component;

import elemental2.core.Function;
import elemental2.dom.DomGlobal.SetTimeoutCallbackFn;
import elemental2.dom.HTMLDivElement;
import lombok.experimental.Delegate;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Arrays;
import java.util.function.Supplier;

import static org.jboss.elemento.Elements.div;

@Singleton
public class Console implements IsElement<HTMLDivElement>, Logger {
    @Delegate final HTMLContainerBuilder<HTMLDivElement> _this = div().css("console");
    @Inject public Console() {}
    private Line last = null;
    public void type(String text) {
        close();
        if(text.contains("\n")) {
            int index = text.indexOf("\n");
            String first = text.substring(0, index);
            String remains = text.substring(index+1);
            last = Line.type(first, ()->{
                type(remains);
                return null;
            });
        } else last = Line.type(text, null);
        add(last);
        element().scrollTop = element().scrollHeight;
    }
    @Override
    public void print(String text) {
        Arrays.stream(text.split("\n")).map(Line::print).forEach(line->{
            close();
            last = line;
            add(line);
        });
        element().scrollTop = element().scrollHeight;
    }
    public void close() {
        if(last!=null) last.close();
    }
    private static final class Line implements IsElement<HTMLDivElement> {
        private static Line type(String text, Supplier<Void> onComplete) {
            var line = new Line();
            var iter = text.chars().mapToObj(c -> (char) c).map(c->{
                if(c == '\n') return "<br/>";
                else return c;
            }).iterator();
            repeatUntil(args->line.add(iter.next().toString()), 50, iter::hasNext, onComplete);
            return line;
        }
        private static Line print(String text) {
            var line = new Line();
            line.element().innerHTML = text;
            return line;
        }
        @Delegate final HTMLContainerBuilder<HTMLDivElement> _this = div().css("line");
        public Line() {
            this.element().innerHTML = "";
        }
        private void close() {
            element().style.borderRight = "0px hidden";
        }
        static native Function repeatUntil(SetTimeoutCallbackFn func, double step, Supplier<Boolean> check, Supplier<Void> onComplete) /*-{
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
