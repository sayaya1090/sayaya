package net.sayaya.client.edit.handler;

import elemental2.dom.DomGlobal;
import elemental2.dom.KeyboardEvent;
import jsinterop.base.Js;
import net.sayaya.ui.dom.MdTextFieldElement;

public interface KeyEventHandler {
    boolean handle(KeyboardEvent evt, MdTextFieldElement editor);

    default void insertText(String value) {
        Document doc = Js.uncheckedCast(DomGlobal.document);
        doc.execCommand("insertText", false, value);
    }
}
