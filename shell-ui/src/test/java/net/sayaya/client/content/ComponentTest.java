package net.sayaya.client.content;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
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
        progressBar();
        drawer(components);
    }
    private void progressBar() {
        var progress = customContainer("sac-progress", ProgressElement.class);
        body().add(progress);
        var data = new Progress();
        data.value(0, 0);
        data.intermediate();
        progress.element().update(data);
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
