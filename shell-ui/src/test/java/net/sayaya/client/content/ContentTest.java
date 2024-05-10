package net.sayaya.client.content;

import com.google.gwt.core.client.EntryPoint;

import static org.jboss.elemento.Elements.body;
import static org.jboss.elemento.Elements.iframe;

public class ContentTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        var body = body().add(iframe().id("content").css("content").style("top: 3rem;"));
        var components = DaggerContentTestComponent.create();
        body.add(components.progressElement())
            .add(components.contentElement());
    }
}
