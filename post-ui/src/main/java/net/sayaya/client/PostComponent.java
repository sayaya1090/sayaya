package net.sayaya.client;

import net.sayaya.client.list.PostListModule;
import net.sayaya.client.list.PostListScene;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { PostModule.class, PostListModule.class })
public interface PostComponent {
    PostListScene postListScene();
}
