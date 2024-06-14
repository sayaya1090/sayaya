package net.sayaya.client.content.dom;

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
    @JsMethod void onHashChange(String hash);
    @JsMethod void attach(MutationRecord mutationRecord);
    @JsMethod void detach(MutationRecord mutationRecord);
    @JsMethod void urlSubject(Object observer);
    @JsMethod void progressSubject(Object observer);
    @JsIgnore @JsOverlay static void onHashChange(HTMLElement elem, String hash) {
        try { ((IsFrame)Js.uncheckedCast(elem)).onHashChange(hash); } catch(Exception ignore) {}
    }
    @JsIgnore @JsOverlay static void attach(HTMLElement elem) {
        try { ((IsFrame) Js.uncheckedCast(elem)).attach((MutationRecord) null); } catch(Exception ignore) {}
    }
    @JsIgnore @JsOverlay static void detach(HTMLElement elem) {
        try { ((IsFrame)Js.uncheckedCast(elem)).detach((MutationRecord) null); } catch(Exception ignore) {}
    }
    @JsIgnore @JsOverlay static void urlSubject(HTMLElement elem, BehaviorSubjectJs<String> url) {
        try { ((IsFrame)Js.uncheckedCast(elem)).urlSubject(url); } catch(Exception ignore) {}
    }
    @JsIgnore @JsOverlay static void progressSubject(HTMLElement elem, BehaviorSubjectJs<Progress> progress) {
        try { ((IsFrame)Js.uncheckedCast(elem)).progressSubject(progress); } catch(Exception ignore) {}
    }
}
