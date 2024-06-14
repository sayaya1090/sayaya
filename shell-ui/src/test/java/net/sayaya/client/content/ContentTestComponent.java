package net.sayaya.client.content;

import net.sayaya.client.content.component.ProgressElementBuilder;
import net.sayaya.client.data.Progress;
import net.sayaya.rx.subject.BehaviorSubjectJs;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { ContentTestModule.class, ContentModule.class })
public interface ContentTestComponent {
    ProgressElementBuilder progressElement();
    Progress progress();
    ContentElement contentElement();
    @Named("url") BehaviorSubjectJs<String> urlChangeSubject();
}
