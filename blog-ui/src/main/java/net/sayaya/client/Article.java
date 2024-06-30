package net.sayaya.client;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import net.sayaya.client.dom.ArticleListSceneElement;
import net.sayaya.client.dom.ArticleSceneElement;
import net.sayaya.client.dom.CustomElements;

import java.util.Arrays;

public class Article implements EntryPoint {
    @Override
    public void onModuleLoad() {
        var components = DaggerArticleComponent.create();
        CustomElements.define("sac-articles", ArticleListSceneElement.class, instance-> ArticleListSceneElement.initialize(instance, components.list()));
        CustomElements.define("sac-article", ArticleSceneElement.class, instance-> ArticleSceneElement.initialize(instance));
        Arrays.stream(components.articles().getValue()).forEach(d-> DomGlobal.console.log(d));
    }
}