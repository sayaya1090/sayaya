package net.sayaya.client;

import net.sayaya.client.api.PostApi;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.client.data.Post;
import net.sayaya.client.edit.PostEditModule;
import net.sayaya.client.edit.PostEditScene;
import net.sayaya.client.list.PostListModule;
import net.sayaya.client.list.PostListScene;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { PostModule.class, PostListModule.class, PostEditModule.class })
public interface PostComponent {
    PostListScene postListScene();
    PostEditScene postEditScene();
    PostApi postApi();
    BehaviorSubject<Post> post();
    BehaviorSubject<CatalogItem> catalog();
    @Named("is-preview-mode") BehaviorSubject<Boolean> isPreviewMode();
}
