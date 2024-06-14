package net.sayaya.client.content;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import net.sayaya.client.content.component.ProgressElementBuilder;
import net.sayaya.client.content.dom.ProgressElement;
import net.sayaya.client.data.Progress;

import static net.sayaya.client.dom.CustomElements.customContainer;
import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static org.jboss.elemento.Elements.body;

public class ComponentTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        ContentModule.defineCustomTags();
        var components = DaggerContentTestComponent.create();
        changeUrlButton(components);
        progressBar(components.progressElement(), components.progress());
        drawer(components);
    }
    private void progressBar(ProgressElementBuilder elem, Progress value) {
        body().add(elem);
        value.enabled(true).intermediate(true);
    }
    private void drawer(ContentTestComponent components) {
        body().add(components.contentElement());
    }
    private void changeUrlButton(ContentTestComponent components) {
        var url = components.urlChangeSubject();
        DomGlobal.console.log(url);
        var testSrc = button().text().id("test").add("Test");
        body().add(testSrc);
        testSrc.onClick(evt->url.next("menu1-page2"));
    }
}
