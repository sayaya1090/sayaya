package net.sayaya.client.edit;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import net.sayaya.client.data.GithubRepositoryConfig;
import net.sayaya.client.data.JsWindow;
import net.sayaya.client.data.Post;
import net.sayaya.client.data.Progress;
import net.sayaya.client.dom.CustomElements;
import net.sayaya.client.dom.PostEditSceneElement;
import net.sayaya.rx.subject.BehaviorSubjectJs;
import org.jboss.elemento.InputType;

import static elemental2.core.Global.JSON;
import static net.sayaya.client.dom.CustomElements.customContainer;
import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static org.jboss.elemento.Elements.body;
import static org.jboss.elemento.Elements.input;

public class PostEditTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        JsWindow.url = new BehaviorSubjectJs<>("");
        JsWindow.progress = new Progress();

        var components = DaggerPostEditComponent.create();
        var scene = components.postEditScene();
        CustomElements.define("sac-post-edit", PostEditSceneElement.class, instance->
                PostEditSceneElement.initialize(instance, scene, components.api(), components.post(), components.catalog(), components.isPreviewMode()));
        var elem = customContainer("sac-post-edit", PostEditSceneElement.class);
        body().add(elem);
        elem.element().attach(null);

        var post = components.post();
        post.next(new Post().title("Title"));

        var btnSetupGithubRepoConfig = button().text().add("Set Github Repo Config").id("setup-github-repo-config");
        var q = components.githubRepoConfig();
        btnSetupGithubRepoConfig.onClick(evt->{
            DomGlobal.console.log(q);
            q.next(new GithubRepositoryConfig().repo("Repo1").branch("branch1").owner("user").auth(true));
        });
        body().add(btnSetupGithubRepoConfig);
        var btnClearGithubRepoConfig = button().text().add("Clear Github Repo Config").id("clear-github-repo-config");
        btnClearGithubRepoConfig.onClick(evt->q.next(new GithubRepositoryConfig()));
        body().add(btnClearGithubRepoConfig);

        var json = input(InputType.textarea).id("post-json").style("position:absolute; z-index:999999;");
        components.title().subscribe(a-> json.element().value = JSON.stringify(post.getValue()));
        components.markdown().subscribe(a-> json.element().value = JSON.stringify(post.getValue()));
        body().add(json);
    }
}
