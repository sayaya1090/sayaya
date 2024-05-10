package net.sayaya.client.url;

import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { ContentUrlChangeTestModule.class, UrlModule.class })
public interface ContentUrlChangeTestComponent {
    UrlChangeListener urlChangeListener();
    @Named("contentUrl") BehaviorSubject<String> urlChangeSubject();
}
