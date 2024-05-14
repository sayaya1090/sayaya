package net.sayaya.client;

import net.sayaya.client.component.LoginElementBuilder;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = LoginModule.class)
public interface LoginComponent {
    LoginElementBuilder login();
}
