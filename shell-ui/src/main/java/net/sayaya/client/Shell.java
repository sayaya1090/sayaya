package net.sayaya.client;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.content.ContentModule;

import static org.jboss.elemento.Elements.body;

public class Shell implements EntryPoint {
    @Override
    public void onModuleLoad() {
        ContentModule.defineCustomTags();
        var components = DaggerShellComponent.create();
        components.urlChangeListener().listen();
        body().add(components.progressElement())
              .add(components.contentElement());
    }
}
