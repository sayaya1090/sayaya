package net.sayaya.client.edit;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.data.Post;
import net.sayaya.client.dom.CustomElements;
import net.sayaya.client.dom.PostEditSceneElement;
import net.sayaya.client.dom.PostListSceneElement;
import net.sayaya.client.list.DaggerPostListComponent;

import static net.sayaya.client.dom.CustomElements.customContainer;
import static org.jboss.elemento.Elements.body;

public class PostEditTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        var components = DaggerPostEditComponent.create();
        var scene = components.postEditScene();
        CustomElements.define("sac-post-edit", PostEditSceneElement.class, instance-> PostEditSceneElement.initialize(instance, scene));
        var elem = customContainer("sac-post-edit", PostEditSceneElement.class);
        body().add(elem);

        var post = components.post();
        post.next(new Post().title("Title"));
    }
}
