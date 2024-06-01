package net.sayaya.client;

import net.sayaya.client.edit.PostEditModule;
import net.sayaya.client.edit.PostEditScene;
import net.sayaya.client.list.PostListModule;
import net.sayaya.client.list.PostListScene;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { PostModule.class, PostListModule.class, PostEditModule.class })
public interface PostComponent {
    PostListScene postListScene();
    PostEditScene postEditScene();
}
