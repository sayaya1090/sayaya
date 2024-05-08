package net.sayaya.client.url;

import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class UrlChangeSubjectWrapper {
    public final BehaviorSubject<String> contentUrl;
    @Inject UrlChangeSubjectWrapper(@Named("contentUrl") BehaviorSubject<String> contentUrl) {
        this.contentUrl = contentUrl;
    }
}
