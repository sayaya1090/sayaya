package net.sayaya.client.util;

import jsinterop.annotations.*;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name="marked")
public class Marked {
    @JsIgnore @JsOverlay
    public static void initialize() {
        var markedOption = JsPropertyMap.of();
        markedOption.set("gfm", true);
        markedOption.set("tables", true);
        markedOption.set("breaks", true);
        markedOption.set("smartLists", true);
        markedOption.set("autolink", true);
        Marked.setOptions(markedOption);

        var highlightOption = JsPropertyMap.of();
        highlightOption.set("langPrefix", "language-");
        HighlightFunction highlight = (code, lang) -> {
            if (lang == null || lang.trim().isEmpty()) lang = "plaintext";
            else lang = Hljs.getLanguage(lang) != null ? lang : "plaintext";
            var param = new Hljs.HighlightParam();
            param.language = lang;
            param.ignoreIllegals = true;
            return Hljs.highlight(code, param).value;
        };
        highlightOption.set("highlight", highlight);
        Marked.use(new MarkedHighlight(highlightOption));
    }
    @JsMethod public native static String parse(String markdown);
    @JsMethod public native static void setOptions(JsPropertyMap<Object> option);
    @JsMethod public native static void use(MarkedHighlight option);
    @JsType(isNative = true, namespace = "markedHighlight", name="markedHighlight")
    public static class MarkedHighlight {
        @JsConstructor
        public MarkedHighlight(JsPropertyMap<?> option) {}
    }

    @JsFunction
    public interface HighlightFunction {
        String apply(String code, String lang);
    }
}