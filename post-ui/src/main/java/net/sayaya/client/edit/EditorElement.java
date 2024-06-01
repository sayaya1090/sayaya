package net.sayaya.client.edit;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import lombok.experimental.Delegate;
import net.sayaya.rx.subject.BehaviorSubject;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.jboss.elemento.Elements.div;

@Singleton
public class EditorElement implements IsElement<HTMLElement> {
    @Delegate private final HTMLContainerBuilder<HTMLDivElement> elem = div();
    @Inject EditorElement(MarkdownEditorElement markdownEditor,
                          PreviewElement preview, @Named("is-preview-mode") BehaviorSubject<Boolean> isPreviewMode,
                          PublishElement publish, @Named("is-publish-mode") BehaviorSubject<Boolean> isPublishMode) {
        this.css("content-editor")
            .add(markdownEditor)
            .add(preview)
            .add(publish);
        isPreviewMode.subscribe(isPreview->{
            if(isPreview) attr("preview", "true");
            else element().removeAttribute("preview");
        });
        isPublishMode.subscribe(isPublish->{
            if(isPublish) attr("publish", "true");
            else element().removeAttribute("publish");
        });
    }
}
