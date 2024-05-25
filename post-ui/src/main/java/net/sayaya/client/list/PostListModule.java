package net.sayaya.client.list;

import dagger.Provides;
import net.sayaya.client.dom.ContainerBuilder;
import net.sayaya.client.dom.CustomElement;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.client.dom.CustomElements.customContainer;
import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class PostListModule {
    @Provides @Singleton ContainerBuilder<CustomElement> postCardContainer() {
        return customContainer("sac-post-card-container", CustomElement.class);
    }
    @Provides @Singleton @Named("sort-by") static BehaviorSubject<String> sortBy() {
        return behavior("createdAt");
    }
    @Provides @Singleton @Named("sort") static BehaviorSubject<String> sort() {
        return behavior("false");
    }

}

