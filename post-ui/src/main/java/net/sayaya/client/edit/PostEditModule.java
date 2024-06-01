package net.sayaya.client.edit;

import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import elemental2.dom.HTMLElement;
import net.sayaya.client.api.GithubApi;
import net.sayaya.client.data.*;
import net.sayaya.client.edit.handler.*;
import net.sayaya.client.util.Marked;
import net.sayaya.rx.subject.BehaviorSubject;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static net.sayaya.client.util.Debounce.debounce;
import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static org.jboss.elemento.Elements.htmlContainer;

@dagger.Module
public class PostEditModule {
    @Provides @Singleton static BehaviorSubject<Post> post(
            @Named("title") BehaviorSubject<String> title,
            @Named("markdown") BehaviorSubject<String> markdown,
            BehaviorSubject<List<Image>> images
    ) {
        var behavior = behavior(new Post());
        behavior.subscribe(p->{
            if(p.title!=null && !p.title.equals(title.getValue())) title.next(p.title);
            if(p.markdown!=null && !p.markdown.equals(markdown.getValue())) markdown.next(p.markdown);
            if(isDifferent(p.images, images.getValue())) images.next(p.images != null ?Arrays.stream(p.images).collect(Collectors.toList()):new LinkedList<>());
        });
        title.subscribe(evt->{
            var post = behavior.getValue();
            if(post==null) return;
            post.title(evt);
        });
        markdown.subscribe(evt->{
            var post = behavior.getValue();
            if(post==null) return;
            post.markdown(evt);
        });
        images.subscribe(evt->{
            var post = behavior.getValue();
            if(post==null) return;
            if(isDifferent(post.images, evt)) post.images(evt.stream().toArray(Image[]::new));
        });
        return behavior;
    }
    @Provides @Singleton static BehaviorSubject<PostRequest> request(BehaviorSubject<Post> post, BehaviorSubject<Commit> commit) {
        var behavior = behavior(new PostRequest());
        behavior.subscribe(p->{
            if(p.post!=null && !p.post.equals(post.getValue())) post.next(p.post);
            if(p.commit!=null && !p.commit.equals(commit.getValue())) commit.next(p.commit);
        });
        post.subscribe(p->behavior.next(behavior.getValue().post(p)));
        commit.subscribe(c->behavior.next(behavior.getValue().commit(c)));
        return behavior;
    }
    @Provides @Singleton static BehaviorSubject<CatalogItem> catalog(@Named("title") BehaviorSubject<String> title,
                                                                     @Named("tags") BehaviorSubject<Set<String>> tags) {
        var behavior = behavior(new CatalogItem());
        behavior.subscribe(c->{
            if(c.title!=null && !c.title.equals(title.getValue())) title.next(c.title);
            if(isDifferent(c.tags, tags.getValue())) tags.next(Arrays.stream(c.tags).collect(Collectors.toSet()));
        });
        title.subscribe(t-> behavior.next(behavior.getValue().title(t)));
        tags.subscribe(t -> {
            if(isDifferent(behavior.getValue().tags, t)) behavior.next(behavior.getValue().tags(t.stream().toArray(String[]::new)));
        });
        return behavior;
    }
    private static <T> boolean isDifferent(T[] array, Collection<T> collection) {
        int length1 = array!=null?array.length:0;
        int length2 = collection!=null?collection.size():0;
        if(length1!=length2) return true;
        if(array!=null) for(T t: array) if(!collection.contains(t)) return true;
        return false;
    }
    @Provides @Singleton @Named("title") static BehaviorSubject<String> title() {
        return behavior("");
    }
    @Provides @Singleton @Named("markdown") static BehaviorSubject<String> markdown() {
        return behavior("");
    }
    @Provides @Singleton @Named("html") static BehaviorSubject<String> html(@Named("markdown") BehaviorSubject<String> markdown, BehaviorSubject<List<Image>> images, BehaviorSubject<Post> post) {
        var subject = behavior("");
        var initialized = new AtomicBoolean(false);
        var update = debounce(()->{
            var md = markdown.getValue();
            if(!initialized.get()) try {
                Marked.initialize();
                initialized.set(true);
            } catch(Exception ignore) {}
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
    @Provides @Singleton static BehaviorSubject<List<Image>> images() {
        return behavior(new LinkedList<>());
    }
    @Provides @Singleton static BehaviorSubject<GithubRepositoryConfig> repoConfig(GithubApi api) {
        return behavior(null);
    }
    @Provides @Singleton static BehaviorSubject<Commit> commit(BehaviorSubject<GithubRepositoryConfig> repo) {
        var behavior = behavior((Commit)null);
        repo.subscribe(dest->{
            if(dest==null || !dest.isValid()) behavior.next(null);
            else {
                var commit = behavior.getValue();
                if(commit==null) {
                    commit = new Commit().msg("Initial commit");
                    behavior.next(commit);
                }
                commit.destination = new GithubRepositoryConfig().owner(dest.owner).repo(dest.repo).branch(dest.branch);
            }
        });
        return behavior;
    }
    @Provides @ElementsIntoSet static Set<KeyEventHandler> keyHandlers() {
        return new HashSet<>(Arrays.asList(new ListOnEnter(), new ListOrdinalOnEnter(), new ListOnTab(), new ListOrdinalOnTab()));
    }
    @Provides @ElementsIntoSet static Set<PasteEventHandler> pasteHandlers(BehaviorSubject<List<Image>> images) {
        return new HashSet<>(Arrays.asList((PasteEventHandler)new ImagePaste(images)));
    }
    @Provides @Singleton @Named("is-preview-mode") static BehaviorSubject<Boolean> isPreviewMode() {
        return behavior(true);
    }
    @Provides @Singleton @Named("is-publish-mode") static BehaviorSubject<Boolean> isPublishMode() {
        return behavior(false);
    }
    @Provides @Singleton @Named("post-card") HTMLContainerBuilder<HTMLElement> postCard() {
        return htmlContainer("sac-post-card", HTMLElement.class);
    }
    @Provides @Singleton @Named("tags") static BehaviorSubject<Set<String>> tags() {
        return behavior(new HashSet<>());
    }
}
