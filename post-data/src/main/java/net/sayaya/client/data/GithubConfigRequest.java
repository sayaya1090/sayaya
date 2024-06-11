package net.sayaya.client.data;

import jsinterop.annotations.*;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Setter(onMethod_ = { @JsOverlay, @JsIgnore} )
@Accessors(fluent = true)
public final class GithubConfigRequest {
    @JsProperty(name="app_id")
    public String appId;
    @JsProperty(name="private_key")
    public String privateKey;
    @JsProperty(name="installation_id")
    public String installationId;
    public String owner;
    public String repo;
    public String branch;
}
