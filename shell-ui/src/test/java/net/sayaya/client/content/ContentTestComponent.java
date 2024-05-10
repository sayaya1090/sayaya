package net.sayaya.client.content;

import net.sayaya.client.url.UrlModule;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { ContentTestModule.class, ContentModule.class })
public interface ContentTestComponent {
    ContentElement contentElement();
    ProgressElement progressElement();
}
