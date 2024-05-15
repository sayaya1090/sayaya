package net.sayaya.client.url;

import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { UrlChangeTestModule.class, UrlModule.class })
public interface UrlChangeTestComponent {
    UrlChangeListener urlChangeListener();
    @Named("url") BehaviorSubject<String> urlChangeSubject();
}
