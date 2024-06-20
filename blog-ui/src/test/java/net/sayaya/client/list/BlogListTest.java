package net.sayaya.client.list;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.data.JsWindow;
import net.sayaya.client.data.Progress;
import net.sayaya.rx.subject.BehaviorSubjectJs;

public class BlogListTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        JsWindow.url = new BehaviorSubjectJs<>("");
        JsWindow.progress = new Progress();

        /*var components = DaggerPostListComponent.create();
        var scene = components.postListScene();

        CustomElements.define("sac-post-list", PostListSceneElement.class, instance-> PostListSceneElement.initialize(instance, scene));
        var elem = customContainer("sac-post-list", PostListSceneElement.class);
        body().add(elem);*/
    }
}
