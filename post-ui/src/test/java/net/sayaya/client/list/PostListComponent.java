package net.sayaya.client.list;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { PostListModule.class, PostListTestModule.class })
public interface PostListComponent {
    PostListScene postListScene();
}