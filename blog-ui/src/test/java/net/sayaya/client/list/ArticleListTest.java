package net.sayaya.client.list;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.data.JsWindow;
import net.sayaya.client.data.Progress;
import net.sayaya.client.dom.ArticleListSceneElement;
import net.sayaya.client.dom.CustomElements;
import net.sayaya.rx.subject.BehaviorSubjectJs;

import static net.sayaya.client.dom.CustomElements.customContainer;
import static org.jboss.elemento.Elements.body;

public class ArticleListTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        JsWindow.url = new BehaviorSubjectJs<>("");
        JsWindow.progress = new Progress();

        var components = DaggerArticleListComponent.create();
        var scene = components.articleListScene();

        CustomElements.define("sac-articles", ArticleListSceneElement.class, instance-> ArticleListSceneElement.initialize(instance, scene));
        var elem = customContainer("sac-articles", ArticleListSceneElement.class);
        body().add(elem);
    }
}
