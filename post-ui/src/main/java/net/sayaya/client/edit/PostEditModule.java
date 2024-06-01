package net.sayaya.client.edit;

import dagger.Provides;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.client.data.Image;
import net.sayaya.client.data.Post;
import net.sayaya.client.util.Marked;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

import java.util.List;

import static net.sayaya.client.util.Debounce.debounce;
import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class PostEditModule {
    @Provides @Singleton static BehaviorSubject<Post> post() {
        return behavior(new Post());
    }
    @Provides @Singleton static BehaviorSubject<CatalogItem> catalog() {
        return behavior(new CatalogItem());
    }
    @Provides @Singleton @Named("title") static BehaviorSubject<String> title(BehaviorSubject<Post> post) {
        var subject = behavior("");
        post.subscribe(p->subject.next(p.title));
        subject.subscribe(evt->{
            var p = post.getValue();
            if(p==null) return;
            p.title(evt);
        });
        return subject;
    }

    @Provides @Singleton @Named("markdown") static BehaviorSubject<String> markdown(BehaviorSubject<Post> post) {
        var subject = behavior("");
        post.subscribe(p->subject.next(p.markdown));
        subject.subscribe(evt->{
            var p = post.getValue();
            if(p==null) return;
            p.markdown(evt);
        });
        return subject;
    }
    @Provides @Singleton @Named("html") static BehaviorSubject<String> html(@Named("markdown") BehaviorSubject<String> markdown, BehaviorSubject<List<Image>> images, BehaviorSubject<Post> post) {
        var subject = behavior("");
        var update = debounce(()->{
            var md = markdown.getValue();
            if(md==null || md.isEmpty()) subject.next("");
            else {
                post.getValue().html(Marked.parse(md));        // image 처리 안 된 html
                for (var img : images.getValue()) {
                    var origin = "!\\[\\s*" + img.id + "\\s*]\\(\\s*" + img.name + "\\s*\\)";
                    var replacement = "![" + img.id + "](" + img.base64 + ")";
                    md = md.replaceAll(origin, replacement);
                }
                subject.next(Marked.parse(md));                // image 처리 된 html
            }
        }, 100);
        markdown.subscribe(evt->update.run());
        images.subscribe(evt->update.run());
        return subject;
    }
}
