package net.sayaya.client;

import net.sayaya.client.component.LoginBox;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = LoginModule.class)
public interface LoginComponent {
    LoginBox loginBox();
}
