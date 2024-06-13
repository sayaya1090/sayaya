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
    public String base64Decoded;    // img src에서 사용하는 base64로 저장된 경우, S3에 저장한 다음 base64를 디코딩한 결과를 저장한다.
}
