package net.sayaya.client.edit.handler;

import net.sayaya.ui.dom.MdTextFieldElement;
import net.sayaya.ui.elements.IconButtonElementBuilder.PlainIconButtonElementBuilder;

import static net.sayaya.ui.elements.IconElementBuilder.icon;

public class Code implements ButtonHandler {
    @Override
    public PlainIconButtonElementBuilder forEditor(MdTextFieldElement editor) {
        var btn = net.sayaya.ui.elements.ButtonElementBuilder.button().icon().add(icon().css("fa-sharp", "fa-light", "fa-code-simple"));
        btn.onClick(evt->{
            if(editor.selectionStart!=editor.selectionEnd) around(editor, "`");
            else around(editor, "\n```\n");
        });
        return btn;
    }
}
