package net.sayaya.client;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.dom.ArticleListSceneElement;
import net.sayaya.client.dom.ArticleSceneElement;
import net.sayaya.client.dom.CustomElements;

public class Article implements EntryPoint {
    @Override
    public void onModuleLoad() {
        var components = DaggerArticleComponent.create();
        CustomElements.define("sac-articles", ArticleListSceneElement.class, instance-> ArticleListSceneElement.initialize(instance));
        CustomElements.define("sac-article", ArticleSceneElement.class, instance-> ArticleSceneElement.initialize(instance));
    }
}