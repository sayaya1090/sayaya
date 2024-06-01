package net.sayaya.client.edit.handler;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class Document extends elemental2.dom.HTMLDocument {
    public native boolean execCommand(String command, boolean show, String arg);
    public native boolean execCommand(String command, boolean show);
}
