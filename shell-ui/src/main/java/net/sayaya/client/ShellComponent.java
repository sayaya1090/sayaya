package net.sayaya.client;

import net.sayaya.client.content.ContentElement;
import net.sayaya.client.content.ContentModule;
import net.sayaya.client.content.component.ProgressElementBuilder;

import net.sayaya.client.url.UrlModule;
import net.sayaya.client.url.UrlChangeListener;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { ShellModule.class, ContentModule.class, UrlModule.class })
public interface ShellComponent {
    ContentElement contentElement();
    ProgressElementBuilder progressElement();
    UrlChangeListener urlChangeListener();

}
