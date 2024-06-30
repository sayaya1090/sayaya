package net.sayaya.client.list;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { ArticleListModule.class, ArticleListTestModule.class })
public interface ArticleListComponent {
    ArticleListScene articleListScene();
}