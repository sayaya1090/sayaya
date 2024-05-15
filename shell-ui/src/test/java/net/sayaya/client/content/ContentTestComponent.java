package net.sayaya.client.content;

import net.sayaya.client.content.component.DrawerElementBuilder;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { ContentTestModule.class, ContentModule.class })
public interface ContentTestComponent {
    DrawerElementBuilder drawer();
    //ContentElement contentElement();
    //ProgressElement progressElement();
}
