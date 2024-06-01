package net.sayaya.client.edit;

import net.sayaya.client.data.Post;
import net.sayaya.client.list.PostListModule;
import net.sayaya.client.list.PostListScene;
import net.sayaya.client.list.PostListTestModule;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { PostEditModule.class, PostEditTestModule.class })
public interface PostEditComponent {
    PostEditScene postEditScene();
    BehaviorSubject<Post> post();
}
