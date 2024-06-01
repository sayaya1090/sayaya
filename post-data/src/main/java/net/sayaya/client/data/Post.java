package net.sayaya.client.data;

import jsinterop.annotations.*;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Setter(onMethod_ = { @JsOverlay, @JsIgnore } )
@Accessors(fluent = true)
public final class Post {
    public String id;
    public String title;
    public String markdown;
    public String html;
    public Image[] images;
}