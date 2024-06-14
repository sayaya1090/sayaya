package net.sayaya.client;

import jsinterop.annotations.JsType;
import net.sayaya.rx.subject.BehaviorSubjectJs;

@JsType(isNative = true, namespace = "rxjs", name = "BehaviorSubject")
public final class S<T> extends BehaviorSubjectJs<T> {
    public S(T init) {
        super(init);
    }
}
