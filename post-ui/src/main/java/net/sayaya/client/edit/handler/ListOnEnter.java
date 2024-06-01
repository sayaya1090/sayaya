package net.sayaya.client.edit.handler;

import elemental2.core.JsRegExp;
import elemental2.dom.Event;
import elemental2.dom.KeyboardEvent;
import net.sayaya.ui.dom.MdTextFieldElement;

public class ListOnEnter implements KeyEventHandler {
    private final JsRegExp[] regex = new JsRegExp[] {
            new JsRegExp("^(\\s*-\\s\\[\\s+\\]\\s)"),           // - [ ]
            new JsRegExp("^(\\s*-\\s\\[\\s*[xX]\\s*\\]\\s)"),   // - [x], - [X]
            new JsRegExp("^(\\s*\\*\\s)"),                      // *
            new JsRegExp("^(\\s*\\-\\s)"),                      // -
            new JsRegExp("^(\\s*\\+\\s)")                       // +
    };
    @Override
    public boolean handle(KeyboardEvent evt, MdTextFieldElement editor) {
        if(evt.key.equalsIgnoreCase("enter") && handle(editor)) evt.preventDefault();
        return false;
    }
    private boolean handle(MdTextFieldElement editor) {
        var text = editor.value;
        var end = editor.selectionEnd;
        var lineStart = text.lastIndexOf('\n', Math.max(0, editor.selectionStart-1))+1;
        var line = text.substring(lineStart, end);
        for(var reg: regex) {
            var exp = reg.exec(line);
            if(exp!=null) {
                var tag = exp.getAt(1);
                insertText("\n" + tag);
                var cursor = end + tag.length() + 1;
                editor.dispatchEvent(new Event("change"));
                editor.setSelectionRange(cursor, cursor);
                return true;
            }
        }
        return false;
    }
}