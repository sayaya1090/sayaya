package net.sayaya.client;

import net.sayaya.client.api.OAuthApi;
import net.sayaya.client.component.ConsoleElementBuilder;
import net.sayaya.client.data.Progress;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = LoginModule.class)
public interface LoginComponent {
    ConsoleElementBuilder console();
    OAuthApi api();
    BehaviorSubject<Progress> progress();
}
