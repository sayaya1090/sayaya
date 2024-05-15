package net.sayaya.client.component;

import lombok.experimental.Delegate;
import net.sayaya.client.api.OAuthApi;
import net.sayaya.client.dom.ContainerBuilder;
import net.sayaya.client.dom.LoginElement;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import static net.sayaya.client.dom.CustomElements.customContainer;

@Singleton
public class LoginElementBuilder implements IsElement<LoginElement> {
    @Delegate private final ContainerBuilder<LoginElement> _this = customContainer("sac-login-box", LoginElement.class);
    @Inject public LoginElementBuilder(ConsoleElementBuilder console, OAuthApi oauth) {
        _this.element().console(console.element()).api(oauth);
    }
}
