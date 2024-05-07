package net.sayaya.client.data;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
public final class Page {
    public String icon;
    @JsProperty(name="icon_type")
    public String iconType;
    public String title;
    public String uri;
    public String order;
}