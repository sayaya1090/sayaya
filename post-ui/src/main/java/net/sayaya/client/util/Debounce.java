package net.sayaya.client.util;

import elemental2.dom.DomGlobal;

public class Debounce {
    public static Debounce debounce(Function fn, int delay) {
        return new Debounce(fn, delay);
    }
    @FunctionalInterface
    public interface Function {
        void call();
    }
    private final Function fn;
    private final int delay;
    private double timer;
    private Debounce(Function fn, int delay) {
        this.fn = fn;
        this.delay = delay;
    }
    public void run() {
        if(timer>0) DomGlobal.clearTimeout(timer);
        timer = DomGlobal.setTimeout(objs->{
            fn.call();
        }, delay);
    }
}
