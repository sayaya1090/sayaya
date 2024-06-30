package net.sayaya.client;

import net.sayaya.client.data.CatalogItem;
import net.sayaya.client.list.ArticleListModule;
import net.sayaya.client.list.ArticleListScene;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { ArticleModule.class, ArticleListModule.class })
public interface ArticleComponent {
    BehaviorSubject<CatalogItem[]> articles();
    ArticleListScene list();
}
