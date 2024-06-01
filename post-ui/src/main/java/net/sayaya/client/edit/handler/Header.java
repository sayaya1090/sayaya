package net.sayaya.client.edit.handler;

import elemental2.dom.Event;
import net.sayaya.ui.dom.MdTextFieldElement;
import net.sayaya.ui.elements.IconButtonElementBuilder.PlainIconButtonElementBuilder;

import static net.sayaya.ui.elements.IconElementBuilder.icon;

public class Header implements ButtonHandler {
    @Override
    public PlainIconButtonElementBuilder forEditor(MdTextFieldElement editor) {
        var btn = net.sayaya.ui.elements.ButtonElementBuilder.button().icon().add(icon().css("fa-sharp", "fa-light", "fa-header"));
        btn.onClick(evt->start(editor, "# ", "## " ));
        return btn;
    }
}
