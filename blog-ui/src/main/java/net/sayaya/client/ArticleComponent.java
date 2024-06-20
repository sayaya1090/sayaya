package net.sayaya.client;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { ArticleModule.class })
public interface ArticleComponent {

}
