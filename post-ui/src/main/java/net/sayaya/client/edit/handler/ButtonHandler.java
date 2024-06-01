package net.sayaya.client.edit.handler;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import jsinterop.base.Js;
import net.sayaya.ui.dom.MdTextFieldElement;
import net.sayaya.ui.elements.IconButtonElementBuilder.PlainIconButtonElementBuilder;

public interface ButtonHandler {

    PlainIconButtonElementBuilder forEditor(MdTextFieldElement editor);

    // 커서 라인 가장 처음에 태그를 삽입하고 커서를 라인 마지막으로 이동
    // 기존에 동일 태그가 있으면 기존 태그를 두번째 태그로 대체
    default void start(MdTextFieldElement editor, String tag, String replace) {
        int posNewline = editor.value.lastIndexOf('\n', Math.max(0, editor.selectionStart-1));
        int posTag = editor.value.lastIndexOf(tag, Math.max(0, editor.selectionStart-1));
        int start;
        if(posTag > posNewline) {
            insertText(editor, posTag, posTag+tag.length(), replace);
            start = posTag + replace.length();
        } else {
            insertText(editor, posNewline+1, tag);
            start = posNewline+1+tag.length();
        }
        editor.setSelectionRange(start, editor.value.indexOf('\n', start));
        editor.dispatchEvent(new Event("change"));
    }
    // 커서 라인 가장 마지막에 태그를 삽입하고 커서를 라인 마지막으로 이동
    default void end(MdTextFieldElement editor, String tag) {
        var start = editor.value.indexOf('\n', editor.selectionStart);
        if(start < 0) start = editor.value.length();
        insertText(editor, start, tag);
        editor.setSelectionRange(start+tag.length(), start+tag.length());
        editor.dispatchEvent(new Event("change"));
    }
    default void around(MdTextFieldElement editor, String tag) {
        around(editor, tag, tag);
    }
    // 선택위치 좌우로 태그 삽입하고 선택영역 유지
    // 기존에 동일 태그가 있으면 기존 태그를 삭제
    default void around(MdTextFieldElement editor, String startTag, String endTag) {
        var start = editor.selectionStart;
        var end = editor.selectionEnd;
        var text = editor.value.substring(start, end);
        var replace = startTag + text + endTag;
        var ss = Math.max(0, start-startTag.length());
        var ee = Math.min(end+endTag.length(), editor.value.length());
        if(editor.value.substring(ss, ee).equals(replace)) {
            insertText(editor, ss, ee, text);
            editor.setSelectionRange(ss, end-startTag.length());
        } else {
            insertText(editor, start, end, replace);
            editor.setSelectionRange(start + startTag.length(), end + startTag.length());
        }
    }
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
