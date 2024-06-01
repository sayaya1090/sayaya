package net.sayaya.client.edit;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import lombok.experimental.Delegate;
import net.sayaya.client.edit.handler.*;
import net.sayaya.rx.subject.BehaviorSubject;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import java.util.List;

import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.span;

@Singleton
public class MarkdownEditorControllerElement implements IsElement<HTMLElement> {
    @Delegate private final HTMLContainerBuilder<HTMLDivElement> elem = div().style("width: 100%;");
    @Inject MarkdownEditorControllerElement(MarkdownEditorInputElement ipt, @Named("is-publish-mode") BehaviorSubject<Boolean> isPublishMode) {
        var editor = ipt.element();
        var group1 = List.of(
                new Undo().forEditor(editor).id("undo"),
                new Redo().forEditor(editor).id("redo")
        );
        var group2 = List.of(
                new Header().forEditor(editor).id("header"),
                new Bold().forEditor(editor).id("bold"),
                new Italic().forEditor(editor).id("italic"),
                new Strike().forEditor(editor).id("strike")
        );
        var group3 = List.of(
                new Block().forEditor(editor).id("block"),
                new Code().forEditor(editor).id("code"),
                new Hr().forEditor(editor).id("hr"),
                new Link().forEditor(editor).id("link")
        );
        var group4 = List.of(
                new Ul().forEditor(editor).id("ul"),
                new Ol().forEditor(editor).id("ol"),
                new Check().forEditor(editor).id("check")
        );
        var groups = List.of(group1, group2, group3, group4);
        for(var group: groups) {
            group.forEach(this::add);
            if(group!=group4/*getLast*/) this.add(divider());
        }
        isPublishMode.subscribe(mode->{
            for(var group: groups) group.forEach(btn-> btn.element().disabled = mode);
        });
    }
    private static HTMLContainerBuilder<HTMLElement> divider() {
        return span().css("controller-divider");
    }
}
