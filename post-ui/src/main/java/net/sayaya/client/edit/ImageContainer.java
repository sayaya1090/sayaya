package net.sayaya.client.edit;

import elemental2.dom.ClipboardEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.MouseEvent;
import jsinterop.base.Js;
import net.sayaya.client.data.Image;
import net.sayaya.client.edit.handler.Document;
import net.sayaya.client.edit.handler.PasteEventHandler;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.dom.MdTextFieldElement;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

import static net.sayaya.client.edit.ImageElement.image;
import static org.jboss.elemento.Elements.div;

@Singleton
public class ImageContainer extends HTMLContainerBuilder<HTMLDivElement> implements PasteEventHandler {
    private final BehaviorSubject<List<Image>> images;
    private final BehaviorSubject<Boolean> isPublishMode;
    private final MarkdownEditorInputElement editor;
    @Inject ImageContainer(BehaviorSubject<List<Image>> images, MarkdownEditorInputElement editor, @Named("is-publish-mode") BehaviorSubject<Boolean> isPublishMode) {
        super(div().css("image-card-container").element());
        this.images = images;
        this.isPublishMode = isPublishMode;
        this.editor = editor;
        images.subscribe(list->{
            this.element().innerHTML = "";
            for(var img: list) {
                var elem = image(img).onClick(evt->insertImage(evt, img)).onDeleteClick(evt->removeImage(evt, img));
                add(elem);
            }
        });
    }
    private void insertImage(MouseEvent evt, Image img) {
        if(isPublishMode.getValue()) return;
        evt.stopPropagation();
        var start = editor.element().value.indexOf('\n', editor.element().selectionStart);
        if(start < 0) start = editor.element().value.length();
        insertText(editor.element(), start, (start>0?"\n":"")+"![" + img.id + "](" + img.name + ")\n");
    }
    private void removeImage(MouseEvent evt, Image img) {
        if(isPublishMode.getValue()) return;
        evt.stopPropagation();
        images.getValue().remove(img);
        images.next(images.getValue());
    }
    @Override
    public boolean handle(ClipboardEvent evt, MdTextFieldElement editor) {
        return false;
    }
}
