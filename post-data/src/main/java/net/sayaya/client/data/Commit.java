package net.sayaya.client.data;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Setter(onMethod_= {@JsOverlay, @JsIgnore})
@Accessors(fluent = true)
public final class Commit {
    public GithubRepositoryConfig destination;
    public String msg;
}