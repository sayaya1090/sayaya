package net.sayaya.client.edit.handler;

import elemental2.core.JsRegExp;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.KeyboardEvent;
import net.sayaya.ui.dom.MdTextFieldElement;

public class ListOrdinalOnTab implements KeyEventHandler {
    private final JsRegExp[] regex = new JsRegExp[] {
            new JsRegExp("^(\\s*(\\d+)\\.\\s)")                // 1. 2. 3.
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
                var tag = exp.getAt(1);
                var number = exp.getAt(2);
                var replace = "    " + tag.replace(number, "1");
                editor.setSelectionRange(lineStart, lineStart+tag.length());
                insertText(replace);
                var nextNewline = editor.value.indexOf('\n', end+(replace.length()-tag.length()));
                if(nextNewline>0) {
                    int depth = tag.length() - number.length() - 2;
                    reorder(nextNewline+1, depth, Integer.parseInt(number), editor);
                }
                var cursor = lineEnd + 2;
                editor.dispatchEvent(new Event("change"));
                editor.setSelectionRange(cursor, cursor);
                return true;
            }
        }
        return false;
    }
    private void reorder(int start, int depth, int order, MdTextFieldElement editor) {
        var regex = new JsRegExp("^(\\s{" + depth + "}(\\d+)\\.\\s)");
        var end = editor.value.indexOf('\n', start+1);
        if(end < 0) end = editor.value.length();
        if(start >= end) return;
        var line = editor.value.substring(start, end);
        var exp = regex.exec(line);
        if(exp==null) return;
        var tag = exp.getAt(1);
        var number = exp.getAt(2);
        var replace = tag.replace(number, String.valueOf(order));
        editor.setSelectionRange(start, start+tag.length());
        insertText(replace);
        reorder(end+(replace.length()-tag.length())+1, depth, order+1, editor);
    }
}