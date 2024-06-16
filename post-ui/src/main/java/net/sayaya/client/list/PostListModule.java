package net.sayaya.client.list;

import dagger.Provides;
import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import net.sayaya.client.api.PostApi;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.rx.subject.BehaviorSubject;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import java.util.function.Consumer;

import static net.sayaya.client.util.Throttle.throttle;
import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static org.jboss.elemento.Elements.*;

@dagger.Module
public class PostListModule {
    @Provides @Singleton HTMLContainerBuilder<HTMLElement> postCardContainer() {
        return htmlContainer("sac-post-card-container", HTMLElement.class);
    }
    @Provides @Singleton @Named("sort-by") BehaviorSubject<String> sortBy() {
        return behavior("createdAt");
    }
    @Provides @Singleton @Named("sort") BehaviorSubject<String> sort() {
        return behavior("false");
    }
    @Provides @Singleton BehaviorSubject<CatalogItem[]> posts(PostApi api, @Named("sort-by") BehaviorSubject<String> sortBy, @Named("sort") BehaviorSubject<String> sort) {
        BehaviorSubject<CatalogItem[]> behavior = behavior(null);
        var reload = throttle(()->reload(api, behavior, sortBy, sort), 100);
        Consumer<Object> update = evt->reload.run();
        sortBy.subscribe(update::accept);
        sort.subscribe(update::accept);
        reload.run();
        return behavior;
    }
    private void reload(PostApi api, BehaviorSubject<CatalogItem[]> posts, BehaviorSubject<String> sortBy, BehaviorSubject<String> sort) {
        api.search(0, 10, sortBy.getValue(), Boolean.parseBoolean(sort.getValue())).then(item->{
            posts.next(item);
            return null;
        }).catch_(err->{
            DomGlobal.console.error(Global.JSON.stringify(err));
            return null;
        });
    }
}

