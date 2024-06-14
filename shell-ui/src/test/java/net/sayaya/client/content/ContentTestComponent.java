package net.sayaya.client.content;

import net.sayaya.rx.subject.BehaviorSubjectJs;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { ContentTestModule.class, ContentModule.class })
public interface ContentTestComponent {
    ContentElement contentElement();
    @Named("url") BehaviorSubjectJs<String> urlChangeSubject();
}
