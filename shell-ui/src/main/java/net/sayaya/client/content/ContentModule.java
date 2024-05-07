package net.sayaya.client.content;

import dagger.Provides;
import net.sayaya.client.data.Page;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.Subject;

import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static net.sayaya.rx.subject.Subject.subject;

@dagger.Module
public class ContentModule {
    @Provides @Singleton static BehaviorSubject<Progress> provideProgress() {
        Progress impl = new Progress();
        BehaviorSubject<Progress> subject = behavior(impl);
        impl.onValueChange(subject);
        return subject;
    }
    @Provides @Singleton static Subject<Page[]> providePage() {
        return subject(Page[].class);
    }
    @Provides @Singleton @Named("isMenuShown") static BehaviorSubject<Boolean> provideIsMenuShown() {
        return behavior(false);
    }
}
