package net.sayaya.client.data;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
public final class User {
    public String id;				// 사용자 ID
    public String name;
}
