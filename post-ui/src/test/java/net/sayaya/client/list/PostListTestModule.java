package net.sayaya.client.list;

import dagger.Provides;
import net.sayaya.client.api.FetchApi;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Singleton;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class PostListTestModule {
    @Provides FetchApi fetch() { return new FetchApi() {}; }
    @Provides @Singleton BehaviorSubject<CatalogItem> catalogMock() {
        return behavior(new CatalogItem());
    }
}

