package net.sayaya.client.data;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
@Setter(onMethod_ = { @JsOverlay, @JsIgnore} )
@Accessors(fluent = true)
public final class GithubRepositories {
    public String owner;
    public String[] repos;
    @JsIgnore @JsOverlay
    public List<String> asList() {
        return Arrays.stream(repos).collect(Collectors.toList());
    }
}
