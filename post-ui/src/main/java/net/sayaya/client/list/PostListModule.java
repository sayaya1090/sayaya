package net.sayaya.client.list;

import dagger.Provides;
import elemental2.dom.HTMLElement;
import net.sayaya.rx.subject.BehaviorSubject;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static org.jboss.elemento.Elements.*;

@dagger.Module
public class PostListModule {
    @Provides @Singleton HTMLContainerBuilder<HTMLElement> postCardContainer() {
        return htmlContainer("sac-post-card-container", HTMLElement.class);
    }
    @Provides @Singleton @Named("sort-by") static BehaviorSubject<String> sortBy() {
        return behavior("createdAt");
    }
    @Provides @Singleton @Named("sort") static BehaviorSubject<String> sort() {
        return behavior("false");
    }
}

