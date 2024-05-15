package net.sayaya.client.legacy.content;

import dagger.Provides;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
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
    @Provides @Singleton static FrameUpdater provideFrameUpdater(@Named("url") BehaviorSubject<String> contentUrl) {
        return new FrameUpdater(contentUrl);
    }
}
