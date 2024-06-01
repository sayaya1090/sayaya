package net.sayaya.client.edit;

import elemental2.promise.Promise;
import lombok.experimental.Delegate;
import net.sayaya.client.data.GithubRepositoryConfig;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.dom.MdDialogElement;
import net.sayaya.ui.elements.ButtonElementBuilder.TextButtonElementBuilder;
import net.sayaya.ui.elements.DialogElementBuilder;
import net.sayaya.ui.elements.ProgressElementBuilder.LinearProgressElementBuilder;
import net.sayaya.ui.elements.SelectElementBuilder.OutlinedSelectElementBuilder;
import net.sayaya.ui.elements.TextFieldElementBuilder.OutlinedTextFieldElementBuilder;
import org.jboss.elemento.InputType;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static net.sayaya.ui.elements.DialogElementBuilder.dialog;
import static net.sayaya.ui.elements.MenuElementBuilder.Position.Popover;
import static net.sayaya.ui.elements.ProgressElementBuilder.progress;
import static net.sayaya.ui.elements.SelectElementBuilder.select;
import static net.sayaya.ui.elements.TextFieldElementBuilder.textField;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.form;

@Singleton
public class GithubSettingsDialog implements IsElement<MdDialogElement> {
    @Delegate private final DialogElementBuilder dialogSettings = dialog().ariaLabel("GitHub App setting");
    private final LinearProgressElementBuilder progress = progress().linear();
    private final OutlinedTextFieldElementBuilder iptAppId = textField().outlined().label("App ID").required(true);
    private final OutlinedTextFieldElementBuilder iptInstallationId = textField().outlined().label("Installation ID").required(true);
    private final OutlinedTextFieldElementBuilder iptPrivateKey = textField().outlined().type(InputType.textarea).label("RSA Private Key").required(true);
    private final OutlinedTextFieldElementBuilder lblOwner = textField().outlined().label("Owner").disable();
    private final OutlinedSelectElementBuilder iptRepository = select().outlined().label("Repository").required(true).menuPositioning(Popover);
    private final OutlinedSelectElementBuilder iptBranch = select().outlined().label("Branch").required(true).menuPositioning(Popover);
    private final TextButtonElementBuilder btnCancel = button().text().add("CLOSE").icon("close");
    private final TextButtonElementBuilder btnApply = button().text().add("SAVE").icon("save");
    @Inject GithubSettingsDialog(BehaviorSubject<GithubRepositoryConfig> repository) {
        dialogSettings.style("max-width: calc(50em + 48px);")
                .headline("GitHub App Settings")
                .content(progress.style("width: 100%;--_track-color: transparent; padding: 0px;"))
                .content(form()
                        .add(div().style("padding: 1rem; display: flex; justify-content: space-between; align-items: center; gap: 0.5rem;")
                                .add(iptAppId.style("flex-grow: 1;")).add(iptInstallationId.style("flex-grow: 1;")))
                        .add(iptPrivateKey.style("padding-left: 1rem; padding-right: 1rem; width: -webkit-fill-available; height: 6rem;"))
                        .add(div().style("padding: 1rem; display: flex; justify-content: space-between; align-items: center; gap: 0.5rem;")
                                .add(lblOwner.style("flex-grow: 1;")).add(iptRepository.style("flex-grow: 1;")).add(iptBranch.style("flex-grow: 1;"))))
                .actions(form().add(btnCancel).add(btnApply));
        btnCancel.onClick(evt->{
            evt.preventDefault();
            dialogSettings.close();
        });
        btnApply.onClick(evt->{
            evt.preventDefault();
            /*update(GitHubApi.save(auth(), owner.getValue(), iptRepository.value(), iptBranch.value()));*/
            dialogSettings.close();
        });
    }
    private void update(Promise<GithubRepositoryConfig> save, BehaviorSubject<GithubRepositoryConfig> repository) {
        save.then(response->{
            repository.next(response);
            return null;
        }).catch_(err-> {
            repository.next(null);
            return null;
        });
    }
}
