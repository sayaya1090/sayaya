package net.sayaya.client.data;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
public abstract class Message {
    @JsProperty public String type;
}
