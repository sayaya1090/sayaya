package net.sayaya.client;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.dom.CustomElements;
import net.sayaya.client.dom.PostListSceneElement;

public class Post implements EntryPoint {
    @Override
    public void onModuleLoad() {
        CustomElements.define("sac-post-list", PostListSceneElement.class, PostListSceneElement::initialize);
    }
}
