package net.sayaya.client.list;

import net.sayaya.client.data.Progress;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { PostListModule.class, PostListTestModule.class })
public interface PostListComponent {
    PostListScene postListScene();
    @Named("url") BehaviorSubject<String> contentUrl();
    BehaviorSubject<Progress> progress();
}
