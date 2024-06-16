package net.sayaya.client.content.dom;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.MutationRecord;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import net.sayaya.client.data.Progress;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.BehaviorSubjectJs;

@JsType(isNative=true)
public interface IsFrame {
    @JsMethod void attach(MutationRecord mutationRecord);
    @JsMethod void detach(MutationRecord mutationRecord);
    @JsMethod void url(Object observer);
    @JsMethod void progress(Object observer);
    @JsIgnore @JsOverlay static void attach(HTMLElement elem) {
        try { ((IsFrame) Js.uncheckedCast(elem)).attach((MutationRecord) null); } catch(Exception ignore) {}
    }
    @JsIgnore @JsOverlay static void detach(HTMLElement elem) {
        try { ((IsFrame)Js.uncheckedCast(elem)).detach((MutationRecord) null); } catch(Exception ignore) {}
    }
    @JsIgnore @JsOverlay static void url(HTMLElement elem, BehaviorSubjectJs<String> url) {
        try { ((IsFrame)Js.uncheckedCast(elem)).url(url); } catch(Exception ignore) {}
    }
    @JsIgnore @JsOverlay static void progress(HTMLElement elem, Progress progress) {
        try { ((IsFrame)Js.uncheckedCast(elem)).progress(progress); } catch(Exception ignore) {}
    }
}
