package net.sayaya.client.data;

import elemental2.dom.CustomEvent;
import elemental2.dom.Event;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.sayaya.rx.HasValueChangeHandlers;
import org.gwtproject.event.shared.HandlerRegistration;

import java.util.HashSet;
import java.util.Set;

public class Progress implements HasValueChangeHandlers<Progress> {
    @Getter private boolean enabled = false;
    @Getter private boolean intermediate = false;
    private Double value = null;
    @Getter @Accessors(fluent = true)
    private Double max = null;

    public void disable() {
        enabled = false;
        intermediate = false;
        value = null;
        max = null;
        fire();
    }
    public void intermediate() {
        enabled = true;
        intermediate = true;
        value = null;
        max = null;
        fire();
    }
    public void value(double value, double max) {
        enabled = true;
        intermediate = false;
        this.value = value;
        this.max = max;
        fire();
    }
    public Double current() {
        return value;
    }
    private void fire() {
        Event evt;
        try {
            evt = new Event("change");
        } catch(Exception e) {
            evt = new CustomEvent<>("change");
        }
        ValueChangeEvent<Progress> e = ValueChangeEvent.event(evt, this);
        for(ValueChangeEventListener<Progress> listener: listeners) listener.handle(e);
    }
    private final Set<ValueChangeEventListener<Progress>> listeners = new HashSet<>();

    @Override public Progress value() {
        return this;
    }
    @Override public HandlerRegistration onValueChange(ValueChangeEventListener<Progress> listener) {
        listeners.add(listener);
        return ()->listeners.remove(listener);
    }
}
