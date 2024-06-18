package net.sayaya.client.data;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import net.sayaya.rx.subject.BehaviorSubjectJs;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="window")
public final class JsWindow {
    public static Progress progress;
    public static BehaviorSubjectJs<String> url;
}
