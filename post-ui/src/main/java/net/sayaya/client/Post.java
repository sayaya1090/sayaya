package net.sayaya.client;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.dom.CustomElements;
import net.sayaya.client.dom.PostEditSceneElement;
import net.sayaya.client.dom.PostListSceneElement;
import net.sayaya.client.util.Marked;

public class Post implements EntryPoint {
    @Override
    public void onModuleLoad() {
        var components = DaggerPostComponent.create();
        CustomElements.define("sac-post-list", PostListSceneElement.class, instance-> PostListSceneElement.initialize(instance, components.postListScene(), components.contentUrl(), components.progress()));
        CustomElements.define("sac-post-edit", PostEditSceneElement.class, instance-> PostEditSceneElement.initialize(instance, components.postEditScene(), components.contentUrl()));
        Marked.initialize();
    }
}
