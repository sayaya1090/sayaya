package net.sayaya.client.list;

import com.google.gwt.core.client.EntryPoint;

import static org.jboss.elemento.Elements.body;

public class PostListTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        var scene = DaggerPostListComponent.create().postListScene();
        body().add(scene);
    }
}
