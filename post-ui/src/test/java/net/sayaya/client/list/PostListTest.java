package net.sayaya.client.list;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.dom.CustomElements;
import net.sayaya.client.dom.PostListSceneElement;

import static net.sayaya.client.dom.CustomElements.customContainer;
import static org.jboss.elemento.Elements.body;

public class PostListTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        var scene = DaggerPostListComponent.create().postListScene();

        CustomElements.define("sac-post-list", PostListSceneElement.class, instance-> PostListSceneElement.initialize(instance, scene));
        var elem = customContainer("sac-post-list", PostListSceneElement.class);
        body().add(elem);
    }
}
