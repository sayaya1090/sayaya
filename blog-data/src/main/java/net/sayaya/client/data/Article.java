package net.sayaya.client.data;

import jsinterop.annotations.*;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Setter(onMethod_ = { @JsOverlay, @JsIgnore} )
@Accessors(fluent = true)
public final class Article {
    public String id;
    public String title;
    public String author;
    public String description;
    public String[] tags;
    public String thumbnail;
    public String html;
    @JsProperty(name="updated_at") public long updatedAt;
    @JsProperty(name="published_at") public long publishedAt;
}