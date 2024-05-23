package net.sayaya.client.data;

import jsinterop.annotations.*;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Setter(onMethod_ = { @JsOverlay, @JsIgnore } )
@Accessors(fluent = true)
public final class CatalogItem {
    public String id;
    public String title;
    public String[] tags;
    public String description;
    public String thumbnail;
    public String url;
    @JsProperty(name="created_at") public long createdAt;
    public String author;
    @JsProperty(name="updated_at") public long updatedAt;
    @JsProperty(name="published_at") public Double publishedAt;
    public boolean published;
}