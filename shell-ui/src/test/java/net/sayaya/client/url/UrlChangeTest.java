package net.sayaya.client.url;

import com.google.gwt.core.client.EntryPoint;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static org.jboss.elemento.Elements.body;
import static org.jboss.elemento.Elements.iframe;

public class UrlChangeTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        var components = DaggerUrlChangeTestComponent.create();
        var url = components.urlChangeSubject();
        components.urlChangeListener().listen();
        for(var i = 1; i <= 5; ++i) {
            var n = i;
            var testSrc = button().text().id("test"+n).add("Test"+n);
            body().add(testSrc);
            testSrc.onClick(evt->url.next("test"+n));
        }
    }
}
