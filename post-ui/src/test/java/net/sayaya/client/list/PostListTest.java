package net.sayaya.client.list;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.data.JsWindow;
import net.sayaya.client.data.Progress;
import net.sayaya.client.dom.CustomElements;
import net.sayaya.client.dom.PostListSceneElement;
import net.sayaya.rx.subject.BehaviorSubjectJs;

import static net.sayaya.client.dom.CustomElements.customContainer;
import static org.jboss.elemento.Elements.body;

public class PostListTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        JsWindow.url = new BehaviorSubjectJs<>("");
        JsWindow.progress = new Progress();

        var components = DaggerPostListComponent.create();
        var scene = components.postListScene();

        CustomElements.define("sac-post-list", PostListSceneElement.class, instance-> PostListSceneElement.initialize(instance, scene));
        var elem = customContainer("sac-post-list", PostListSceneElement.class);
        body().add(elem);
    }
}
