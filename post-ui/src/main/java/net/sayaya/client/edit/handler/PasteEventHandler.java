package net.sayaya.client.edit.handler;

import elemental2.dom.ClipboardEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import jsinterop.base.Js;
import net.sayaya.ui.dom.MdTextFieldElement;

public interface PasteEventHandler {
    boolean handle(ClipboardEvent evt, MdTextFieldElement editor);
    default void insertText(MdTextFieldElement editor, int pos, String value) {
        insertText(editor, pos, pos, value);
    }
    default void insertText(MdTextFieldElement editor, int start, int end, String value) {
        editor.focus();
        editor.setSelectionRange(start, end);
        Document doc = Js.uncheckedCast(DomGlobal.document);
        doc.execCommand("insertText", false, value);
        editor.dispatchEvent(new Event("change"));
    }
}
