package net.sayaya.client.edit.handler;

import elemental2.core.JsRegExp;
import elemental2.dom.Event;
import elemental2.dom.KeyboardEvent;
import net.sayaya.ui.dom.MdTextFieldElement;

public class ListOnTab implements KeyEventHandler {
    private final JsRegExp[] regex = new JsRegExp[] {
            new JsRegExp("^(\\s*-\\s\\[\\s+\\]\\s)"),           // - [ ]
            new JsRegExp("^(\\s*-\\s\\[\\s*[xX]\\s*\\]\\s)"),   // - [x], - [X]
            new JsRegExp("^(\\s*\\*\\s)"),                      // *
            new JsRegExp("^(\\s*\\-\\s)"),                      // -
            new JsRegExp("^(\\s*\\+\\s)")                       // +
    };

    @Override
    public boolean handle(KeyboardEvent evt, MdTextFieldElement editor) {
        if(evt.key.equalsIgnoreCase("tab") && handle(editor)) evt.preventDefault();
        return false;
    }
    private boolean handle(MdTextFieldElement editor) {
        var text = editor.value;
        var end = editor.selectionEnd;
        var lineStart = text.lastIndexOf('\n', Math.max(0, editor.selectionStart-1))+1;
        var lineEnd = text.indexOf('\n', end);
        if(lineEnd < 0) lineEnd = text.length();
        var line = text.substring(lineStart, lineEnd);
        for(var reg: regex) {
            var exp = reg.exec(line);
            if(exp!=null) {
                editor.setSelectionRange(lineStart, lineStart);
                insertText("    ");
                var cursor = lineEnd + 2;
                editor.dispatchEvent(new Event("change"));
                editor.setSelectionRange(cursor, cursor);
                return true;
            }
        }
        return false;
    }
}