package net.sayaya.client.util;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name="hljs")
public class Hljs {
    @JsMethod public native static String getLanguage(String lang);
    @JsMethod public native static Highlight highlight(String code, HighlightParam param);

    @JsType(isNative = true)
    public static class Highlight {
        public String value;
    }
    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name="Object")
    public static class HighlightParam {
        public String language;
        public boolean ignoreIllegals;
    }
}