package net.sayaya.client.url;

import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { UrlModule.class })
public interface ContentUrlChangeTestComponent {
    UrlChangeListener urlChangeListener();
    UrlChangeSubjectWrapper urlChangeSubject();
}
