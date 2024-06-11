package net.sayaya.client.edit;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLabelElement;
import lombok.experimental.Delegate;
import net.sayaya.client.data.GithubRepositoryConfig;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.elements.IconButtonElementBuilder.PlainIconButtonElementBuilder;
import net.sayaya.ui.elements.IconElementBuilder;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static org.jboss.elemento.Elements.*;

@Singleton
public class GithubSettingsElement implements IsElement<HTMLElement> {
    @Delegate private final HTMLContainerBuilder<HTMLDivElement> div = div().id("github-settings").style("display: flex;align-items: center;");
    private final HTMLContainerBuilder<HTMLLabelElement> lblDestination = label();
    private final PlainIconButtonElementBuilder btnConfig = button().icon();
    @Inject GithubSettingsElement(BehaviorSubject<GithubRepositoryConfig> repository, GithubSettingsDialog dialog) {
        div.add(lblDestination).add(btnConfig).add(dialog);
        btnConfig.onClick(evt-> dialog.open());
        repository.subscribe(this::update);
    }
    private void update(GithubRepositoryConfig config) {
        btnConfig.element().innerHTML = "";
        IconElementBuilder icnConfig = icon();
        if(config==null || !config.isValid()) {
            lblDestination.textContent("No repository assigned");
            div.element().style.color = "var(--md-sys-color-error)";
            icnConfig.css("fa-sharp", "fa-light", "fa-gear");
            icnConfig.element().style.color = "var(--md-sys-color-error)";
        } else {
            var dest = config.owner + "/" + config.repo + ":" + config.branch;
            lblDestination.textContent(dest);
            div.element().style.color = "var(--md-sys-color-safe)";
            icnConfig.css("fa-sharp", "fa-light", "fa-check");
            icnConfig.element().style.color = "var(--md-sys-color-safe)";
        }
        btnConfig.add(icnConfig);
    }
}
