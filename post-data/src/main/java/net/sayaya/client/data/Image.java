package net.sayaya.client.data;

import jsinterop.annotations.*;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Setter(onMethod_ = { @JsOverlay, @JsIgnore })
@Accessors(fluent = true)
public final class Image {
    public String id;
    public String name;
    public String url;
    public String base64;
    @JsProperty(name="base64_decoded")
    public String base64Decoded;  
}
