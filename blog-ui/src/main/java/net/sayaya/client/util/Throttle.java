package net.sayaya.client.util;

import java.util.Date;

public class Throttle {
    public static Throttle throttle(Function fn, int delay) {
        return new Throttle(fn, delay);
    }
    @FunctionalInterface
    public interface Function {
        void call();
    }
    private final Function fn;
    private final int delay;
    private double current = 0;
    private Throttle(Function fn, int delay) {
        this.fn = fn;
        this.delay = delay;
    }
    public void run() {
        var now = new Date().getTime();
        if(now - current >= delay) {
            fn.call();
            current = now;
        }
    }
}