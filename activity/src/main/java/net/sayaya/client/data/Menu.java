package net.sayaya.client.data;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
public final class Menu {
    public String title;
    @JsProperty(name="supporting_text")
    public String supportingText;
    @JsProperty(name="icon_type")
    public String iconType;
    public String icon;
    @JsProperty(name="trailing_text")
    public String trailingText;
    public String order;
    public Page[] children;
}
