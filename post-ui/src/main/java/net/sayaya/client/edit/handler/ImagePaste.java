package net.sayaya.client.edit.handler;

import elemental2.dom.ClipboardEvent;
import elemental2.dom.File;
import elemental2.dom.FileReader;
import net.sayaya.client.data.Image;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.dom.MdTextFieldElement;

import java.util.List;

public class ImagePaste implements PasteEventHandler {
    private final BehaviorSubject<List<Image>> images;
    public ImagePaste(BehaviorSubject<List<Image>> images) {
        this.images = images;
    }
    @Override
    public boolean handle(ClipboardEvent evt, MdTextFieldElement editor) {
        var items = evt.clipboardData.items;
        for(int i = 0; i < items.length; ++i) {
            var item = items.getAt(i);
            if(item.type.startsWith("image")) {
                evt.preventDefault();
                evt.stopPropagation();
                handle(editor, item.getAsFile());
            }
        }
        return false;
    }
    private void handle(MdTextFieldElement editor, File img) {
        var start = editor.value.indexOf('\n', editor.selectionStart);
        if(start < 0) start = editor.value.length();
        FileReader reader = new FileReader();
        var id = "image-" + System.currentTimeMillis();
        reader.onload = evt->{
            var image = new Image().id(id).name(id + ".png").base64(evt.target.result.asString());
            images.getValue().add(image);
            images.next(images.getValue());
            return false;
        };
        reader.readAsDataURL(img);
        insertText(editor, start, (start>0?"\n":"")+"![" + id + "](" + id + ".png)\n");
    }
}
