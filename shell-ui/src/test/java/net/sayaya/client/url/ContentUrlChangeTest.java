package net.sayaya.client.url;

import com.google.gwt.core.client.EntryPoint;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static org.jboss.elemento.Elements.body;
import static org.jboss.elemento.Elements.iframe;

public class ContentUrlChangeTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        body().add(iframe().id("content"));
        var components = DaggerContentUrlChangeTestComponent.create();
        var contentUrl = components.urlChangeSubject();
        components.urlChangeListener().listen();
        for(var i = 1; i <= 5; ++i) {
            var n = i;
            var testSrc = button().text().id("test"+n).add("Test"+n);
            body().add(testSrc);
            testSrc.onClick(evt->contentUrl.next("test"+n));
        }
    }
}
