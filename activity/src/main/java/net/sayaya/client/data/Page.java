package net.sayaya.client.data;

import jsinterop.annotations.*;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Setter(onMethod_ = { @JsOverlay, @JsIgnore } )
@Accessors(fluent = true)
public final class Page {
    public String icon;
    @JsProperty(name="icon_type")
    public String iconType;
    public String title;
    public String uri;
    public String script;
    public String tag;
    public String order;
}