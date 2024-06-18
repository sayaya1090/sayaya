package net.sayaya.client.content.dom;

import elemental2.dom.HTMLElement;
import elemental2.dom.MutationRecord;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative=true)
public interface IsFrame {
    @JsMethod void attach(MutationRecord mutationRecord);
    @JsMethod void detach(MutationRecord mutationRecord);
    @JsIgnore @JsOverlay static void attach(HTMLElement elem) {
        try { ((IsFrame) Js.uncheckedCast(elem)).attach((MutationRecord) null); } catch(Exception ignore) {}
    }
    @JsIgnore @JsOverlay static void detach(HTMLElement elem) {
        try { ((IsFrame)Js.uncheckedCast(elem)).detach((MutationRecord) null); } catch(Exception ignore) {}
    }
}
