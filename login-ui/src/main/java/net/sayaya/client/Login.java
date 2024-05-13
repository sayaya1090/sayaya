package net.sayaya.client;

import com.google.gwt.core.client.EntryPoint;

import static org.jboss.elemento.Elements.body;

public class Login implements EntryPoint {
    @Override
    public void onModuleLoad() {
        body().add(DaggerLoginComponent.create().loginBox());
    }
}
