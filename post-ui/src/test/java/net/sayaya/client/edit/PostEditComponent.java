package net.sayaya.client.edit;

import net.sayaya.client.api.PostApi;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.client.data.GithubRepositoryConfig;
import net.sayaya.client.data.Post;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { PostEditModule.class, PostEditTestModule.class })
public interface PostEditComponent {
    PostEditScene postEditScene();
    PostApi api();
    BehaviorSubject<Post> post();
    BehaviorSubject<CatalogItem> catalog();
    @Named("is-preview-mode") BehaviorSubject<Boolean> isPreviewMode();
    @Named("title") BehaviorSubject<String> title();
    @Named("markdown") BehaviorSubject<String> markdown();
    BehaviorSubject<GithubRepositoryConfig> githubRepoConfig();
}
