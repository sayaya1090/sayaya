package net.sayaya.client.content;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.content.dom.ProgressElement;

import static net.sayaya.client.dom.CustomElements.customContainer;
import static org.jboss.elemento.Elements.body;

public class ComponentTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        ContentModule.defineCustomTags();
        progressBar();
        drawer();
    }
    private void progressBar() {
        var progress = customContainer("sac-progress", ProgressElement.class);
        body().add(progress);
        var data = new Progress();
        data.value(0, 0);
        data.intermediate();
        progress.element().update(data);
    }
    private void drawer() {
        body().add(DaggerContentTestComponent.create().contentElement());
    }
}
