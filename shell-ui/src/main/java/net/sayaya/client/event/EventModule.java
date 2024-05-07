package net.sayaya.client.event;

import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@dagger.Module
public class EventModule {
    @Provides @ElementsIntoSet
    static Set<ModuleEventHandler> provideModuleEventHandlers(@Named("contentUrl") BehaviorSubject<String> contentUrl) {
        var routeHandler = new RouteHandler(contentUrl);
        return new HashSet<>(Arrays.asList(routeHandler));
    }
}
