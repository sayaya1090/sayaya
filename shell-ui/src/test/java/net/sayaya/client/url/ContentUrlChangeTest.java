package net.sayaya.client.url;

import com.google.gwt.core.client.EntryPoint;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static org.jboss.elemento.Elements.body;
import static org.jboss.elemento.Elements.iframe;

public class ContentUrlChangeTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        var iframe = iframe().id("content");
        body().add(iframe);
        var components = DaggerContentUrlChangeTestComponent.create();
        var contentUrl = components.urlChangeSubject().contentUrl;
        components.urlChangeListener().listen();
        var changeSrc = button().text().id("change").add("Change src");
        var clearSrc = button().text().id("clear").add("Clear src");
        body().add(changeSrc).add(clearSrc);
        changeSrc.onClick(evt->contentUrl.next("test"));
        clearSrc.onClick(evt->contentUrl.next(null));
    }
}
