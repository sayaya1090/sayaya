package net.sayaya.client.content;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { ContentTestModule.class, ContentModule.class })
public interface ContentTestComponent {
    ContentElement contentElement();
}
