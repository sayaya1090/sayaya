package net.sayaya.client;

import net.sayaya.client.content.ContentElement;
import net.sayaya.client.content.ContentModule;
import net.sayaya.client.content.ProgressElement;
import net.sayaya.client.event.EventModule;
import net.sayaya.client.event.ModuleEventListener;
import net.sayaya.client.url.UrlModule;
import net.sayaya.client.url.UrlChangeListener;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { ContentModule.class, UrlModule.class, EventModule.class })
public interface ShellComponent {
    ContentElement contentElement();
    ProgressElement progressElement();
    UrlChangeListener urlChangeListener();
    ModuleEventListener moduleEventListener();
}
