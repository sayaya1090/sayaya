package net.sayaya.client.content.dom;

import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.ShadowRootInit;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import net.sayaya.client.data.Progress;
import net.sayaya.client.dom.CustomElement;
import net.sayaya.ui.elements.ProgressElementBuilder;

import static net.sayaya.ui.elements.ProgressElementBuilder.progress;

@JsType
public class ProgressElement extends CustomElement {
    public static void initialize(ProgressElement instance) {
        var options = ShadowRootInit.create();
        options.setMode("open");
        var shadowRoot = instance.attachShadow(options);
        instance.progress = progress().linear();
        shadowRoot.append(instance.progress.element());
    }
    private ProgressElementBuilder.LinearProgressElementBuilder progress;
    @JsIgnore public void update(Progress value) {
        if(!value.enabled) progress.element().style.opacity = CSSProperties.OpacityUnionType.of("0");
        else {
            progress.element().style.opacity = CSSProperties.OpacityUnionType.of("100");
            progress.indeterminate(value.intermediate);
            progress.element().max = value.max;
            if(value.value!=null) progress.value(value.value);
        }
    }
}
