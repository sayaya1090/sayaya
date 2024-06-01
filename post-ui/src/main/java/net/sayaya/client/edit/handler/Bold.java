package net.sayaya.client.edit.handler;

import net.sayaya.ui.dom.MdTextFieldElement;
import net.sayaya.ui.elements.IconButtonElementBuilder.PlainIconButtonElementBuilder;

import javax.inject.Singleton;

import static net.sayaya.ui.elements.IconElementBuilder.icon;

@Singleton
public class Bold implements ButtonHandler {
    @Override
    public PlainIconButtonElementBuilder forEditor(MdTextFieldElement editor) {
        var btn = net.sayaya.ui.elements.ButtonElementBuilder.button().icon().add(icon().css("fa-sharp", "fa-light", "fa-bold"));
        btn.onClick(evt->around(editor, "**"));
        return btn;
    }
}
