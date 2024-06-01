package net.sayaya.client.edit.handler;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import jsinterop.base.Js;
import net.sayaya.ui.dom.MdTextFieldElement;
import net.sayaya.ui.elements.IconButtonElementBuilder.PlainIconButtonElementBuilder;

import static net.sayaya.ui.elements.IconElementBuilder.icon;

public class Undo implements ButtonHandler {
    @Override
    public PlainIconButtonElementBuilder forEditor(MdTextFieldElement editor) {
        var btn = net.sayaya.ui.elements.ButtonElementBuilder.button().icon().add(icon().css("fa-sharp", "fa-light", "fa-redo"));
        btn.onClick(evt->{
            Document doc = Js.uncheckedCast(DomGlobal.document);
            doc.execCommand("undo", false);
            editor.dispatchEvent(new Event("change"));
        });
        return btn;
    }
}
