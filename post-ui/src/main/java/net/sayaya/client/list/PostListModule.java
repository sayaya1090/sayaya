package net.sayaya.client.list;

import dagger.Provides;
import net.sayaya.client.api.FetchApi;
import net.sayaya.client.component.CardContainerElementBuilder;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.client.component.CardContainerElementBuilder.cards;
import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class PostListModule {
    @Provides @Singleton CardContainerElementBuilder postCardContainer() {
        return cards();
    }
    @Provides @Singleton @Named("sort-by") static BehaviorSubject<String> sortBy() {
        return behavior("createdAt");
    }
    @Provides @Singleton @Named("sort") static BehaviorSubject<String> sort() {
        return behavior("false");
    }

}

