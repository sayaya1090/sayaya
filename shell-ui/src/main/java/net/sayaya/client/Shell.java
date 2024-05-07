package net.sayaya.client;

import com.google.gwt.core.client.EntryPoint;

import static org.jboss.elemento.Elements.body;

public class Shell implements EntryPoint {
    @Override
    public void onModuleLoad() {
        var components = DaggerShellComponent.create();
        components.urlChangeListener().listen();
        components.moduleEventListener().listen();
        body().add(components.progressElement())
              .add(components.contentElement());
    }
}
