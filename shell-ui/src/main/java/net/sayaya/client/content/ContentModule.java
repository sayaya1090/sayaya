package net.sayaya.client.content;

import dagger.Provides;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Singleton;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class ContentModule {
    @Provides @Singleton static BehaviorSubject<Progress> provideProgress() {
        Progress impl = new Progress();
        BehaviorSubject<Progress> subject = behavior(impl);
        impl.onValueChange(subject);
        return subject;
    }
}
