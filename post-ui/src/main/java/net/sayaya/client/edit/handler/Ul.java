package net.sayaya.client.edit.handler;

import net.sayaya.ui.dom.MdTextFieldElement;
import net.sayaya.ui.elements.IconButtonElementBuilder.PlainIconButtonElementBuilder;

import static net.sayaya.ui.elements.IconElementBuilder.icon;

public class Ul implements ButtonHandler {
    @Override
    public PlainIconButtonElementBuilder forEditor(MdTextFieldElement editor) {
        var btn = net.sayaya.ui.elements.ButtonElementBuilder.button().icon().add(icon().css("fa-sharp", "fa-light", "fa-list-ul"));
        btn.onClick(evt->start(editor,"* ", ""));
        return btn;
    }
}
