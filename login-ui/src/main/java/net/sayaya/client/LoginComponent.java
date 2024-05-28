package net.sayaya.client;

import net.sayaya.client.api.OAuthApi;
import net.sayaya.client.component.ConsoleElementBuilder;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = LoginModule.class)
public interface LoginComponent {
    ConsoleElementBuilder console();
    OAuthApi api();
}
