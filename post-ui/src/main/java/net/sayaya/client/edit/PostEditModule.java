package net.sayaya.client.edit;

import dagger.Provides;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.client.data.Post;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Singleton;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class PostEditModule {
    @Provides @Singleton static BehaviorSubject<Post> post() {
        return behavior(new Post());
    }
    @Provides @Singleton static BehaviorSubject<CatalogItem> catalog() {
        return behavior(new CatalogItem());
    }
}
