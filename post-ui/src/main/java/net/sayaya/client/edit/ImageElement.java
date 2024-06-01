package net.sayaya.client.edit;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.MouseEvent;
import net.sayaya.client.data.Image;
import net.sayaya.ui.elements.IconButtonElementBuilder.PlainIconButtonElementBuilder;
import org.jboss.elemento.EventCallbackFn;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.HTMLElementBuilder;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static org.jboss.elemento.Elements.*;

public class ImageElement extends HTMLContainerBuilder<HTMLDivElement> {
    static ImageElement image(Image image) {
        return new ImageElement(div(), image);
    }
    private final PlainIconButtonElementBuilder delete = button().icon().css("button").add(icon().css("fa-sharp", "fa-solid", "fa-circle-trash"));
    private ImageElement(HTMLContainerBuilder<HTMLDivElement> div, Image image) {
        super(div.element());
        HTMLElementBuilder<HTMLImageElement> img = img().css("image");
        this.css("image-card").add(img).add(label().add(image.id)).add(delete);
        img.element().src = image.url!=null?image.url:image.base64;
    }
    public ImageElement onClick(EventCallbackFn<MouseEvent> callback) {
        this.on(EventType.click, callback);
        return this;
    }
    public ImageElement onDeleteClick(EventCallbackFn<MouseEvent> callback) {
        delete.on(EventType.click, callback);
        return this;
    }
}
