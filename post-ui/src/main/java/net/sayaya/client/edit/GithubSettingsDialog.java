package net.sayaya.client.edit;

import com.google.gwt.core.client.Scheduler;
import lombok.experimental.Delegate;
import net.sayaya.client.api.GithubApi;
import net.sayaya.client.data.GithubAppConfig;
import net.sayaya.client.data.GithubConfigRequest;
import net.sayaya.client.data.GithubRepositoryConfig;
import net.sayaya.client.util.Throttle;
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

import java.util.List;

import static net.sayaya.client.util.Throttle.throttle;
import static net.sayaya.rx.subject.BehaviorSubject.behavior;
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
    @Delegate private final DialogElementBuilder dialog = dialog().ariaLabel("GitHub App setting");
    private final LinearProgressElementBuilder progress = progress().linear();
    private final OutlinedTextFieldElementBuilder iptAppId = textField().outlined().label("App ID");
    private final OutlinedTextFieldElementBuilder iptInstallationId = textField().outlined().label("Installation ID");
    private final OutlinedTextFieldElementBuilder iptPrivateKey = textField().outlined().type(InputType.textarea).label("RSA Private Key");
    private final OutlinedTextFieldElementBuilder lblOwner = textField().outlined().label("Owner").disable();
    private final OutlinedSelectElementBuilder iptRepository = select().outlined().label("Repository").required(true).menuPositioning(Popover);
    private final OutlinedSelectElementBuilder iptBranch = select().outlined().label("Branch").required(true).menuPositioning(Popover);
    private final TextButtonElementBuilder btnCancel = button().text().add("CLOSE").icon("close");
    private final TextButtonElementBuilder btnApply = button().text().add("SAVE").icon("save");

    private final BehaviorSubject<GithubAppConfig> appConfig = behavior(new GithubAppConfig());
    private final BehaviorSubject<GithubRepositoryConfig> repoConfig;
    private final GithubApi api;
    private final BehaviorSubject<String> owner = behavior(null);
    private final BehaviorSubject<List<String>> repositories = behavior(null);
    private final BehaviorSubject<List<String>> branches = behavior(null);
    @Inject GithubSettingsDialog(BehaviorSubject<GithubRepositoryConfig> repository, GithubApi api) {
        repoConfig = repository;
        this.api = api;
        dialog.style("max-width: calc(50em + 48px);")
              .headline("GitHub App Settings")
              .content(progress.style("width: 100%;--_track-color: transparent; padding: 0px;"))
              .content(form()
                      .add(div().style("padding: 1rem; display: flex; justify-content: space-between; align-items: center; gap: 0.5rem;")
                                .add(iptAppId.style("flex-grow: 1;")).add(iptInstallationId.style("flex-grow: 1;")))
                      .add(iptPrivateKey.style("padding-left: 1rem; padding-right: 1rem; width: -webkit-fill-available; height: 6rem;"))
                      .add(div().style("padding: 1rem; display: flex; justify-content: space-between; align-items: center; gap: 0.5rem;")
                                .add(lblOwner.style("flex-grow: 1;")).add(iptRepository.style("flex-grow: 1;")).add(iptBranch.style("flex-grow: 1;"))))
                .actions(form().add(btnCancel).add(btnApply));
        initialize();
    }
    private final Throttle requestRepoBranch = throttle(this::updateRepoBranch, 300);
    private void initialize() {
        repoConfig.subscribe(evt->requestRepoBranch.run());
        iptAppId.onChange(evt->appConfig.next(appConfig.getValue().appId(iptAppId.value())));
        iptInstallationId.onChange(evt->appConfig.next(appConfig.getValue().installationId(iptInstallationId.value())));
        iptPrivateKey.onChange(evt->appConfig.next(appConfig.getValue().privateKey(iptPrivateKey.value())));
        appConfig.subscribe(evt->requestRepoBranch.run());
        owner.subscribe(owner -> lblOwner.value(owner!=null?owner:""));
        repositories.subscribe(this::updateRepoList);
        branches.subscribe(this::updateBranchList);
        iptRepository.onChange(evt->updateBranch(iptRepository.value()));
        btnCancel.onClick(evt->{
            evt.preventDefault();
            dialog.close();
        });
        btnApply.onClick(evt->{
            evt.preventDefault();
            save();
            dialog.close();
        });
    }
    public void open() {
        dialog.open();
        Scheduler.get().scheduleDeferred(()-> {
            if(!branches.getValue().isEmpty()) iptBranch.element().focus();
            else if(!repositories.getValue().isEmpty()) iptRepository.element().focus();
        });
    }
    private void updateRepoList(List<String> repositories) {
        iptRepository.removeAllOptions();
        if(repositories==null) iptRepository.disable();
        else {
            iptRepository.disable(false);
            for(var repo: repositories) iptRepository.option().value(repo).headline(repo);
            if(repoConfig.getValue()!=null) Scheduler.get().scheduleDeferred(()->iptRepository.element().select(repoConfig.getValue().repo));
        }
    }
    private void updateBranchList(List<String> branches) {
        iptBranch.removeAllOptions();
        if(branches==null) iptBranch.disable();
        else {
            iptBranch.disable(false);
            for(var branch: branches) iptBranch.option().value(branch).headline(branch);
            if(repoConfig.getValue()!=null) Scheduler.get().scheduleDeferred(()->iptBranch.element().select(repoConfig.getValue().branch));
        }
    }
    private void updateRepoBranch() {
        if(isInsufficientConfig()) return;
        progress.indeterminate();
        owner.next(null);
        repositories.next(null);
        branches.next(null);
        iptRepository.reset();
        api.findGithubRepository(appConfig()).then(repos->{
            owner.next(repos.owner);
            repositories.next(repos.asList());
            iptAppId.error(false);
            iptInstallationId.error(false);
            iptPrivateKey.error(false);
            progress.indeterminate(false);
            iptRepository.element().focus();
            if(repoConfig.getValue()!=null) updateBranch(repoConfig.getValue().repo);
            return null;
        }).catch_(err->{
            progress.indeterminate(false);
            iptAppId.error(true);
            iptInstallationId.error(true);
            iptPrivateKey.error(true);
            if(err!=null) iptPrivateKey.errorText(err.toString());
            return null;
        });
    }
    private void updateBranch(String repo) {
        if(isInsufficientConfig()) return;
        progress.indeterminate();
        branches.next(null);
        iptBranch.reset();
        api.findGithubBranch(appConfig(), owner.getValue(), repo).then(list->{
            branches.next(list);
            progress.indeterminate(false);
            iptBranch.element().focus();
            return null;
        }).catch_(err->{
            progress.indeterminate(false);
            //if(err!=null) iptRepository.error(err.toString());
            return null;
        });
    }
    private GithubAppConfig appConfig() {
        var auth = appConfig.getValue();
        if(auth.appId==null || auth.installationId==null || auth.privateKey==null) return null;
        else return auth;
    }
    private boolean isInsufficientConfig() {
        var auth = appConfig.getValue();
        if(auth.appId==null && auth.installationId==null && auth.privateKey==null) return false;
        else return auth.appId == null || auth.installationId == null || auth.privateKey == null;
    }
    private void save() {
        var request = new GithubConfigRequest()
                .owner(owner.getValue())
                .repo(iptRepository.value())
                .branch(iptBranch.value());
        var appConfig = appConfig();
        if(appConfig!=null) request.appId(appConfig.appId)
                .installationId(appConfig.installationId)
                .privateKey(appConfig.privateKey);
        api.save(request).then(response-> {
            repoConfig.next(response);
            return null;
        }).catch_(err-> {
            repoConfig.next(null);
            return null;
        });
    }
}
