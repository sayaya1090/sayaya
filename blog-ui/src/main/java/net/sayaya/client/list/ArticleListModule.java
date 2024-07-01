package net.sayaya.client.list;

import dagger.Provides;
import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import net.sayaya.client.api.ArticleApi;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.rx.subject.BehaviorSubject;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static net.sayaya.client.util.Throttle.throttle;
import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static org.jboss.elemento.Elements.htmlContainer;

@dagger.Module
public class ArticleListModule {
    @Provides @Singleton BehaviorSubject<CatalogItem[]> articles(ArticleApi api, BehaviorSubject<Set<String>> params, @Named("sort-by") BehaviorSubject<String> sortBy, @Named("sort") BehaviorSubject<String> sort) {
        BehaviorSubject<CatalogItem[]> behavior = behavior(getCatalogItemIfPresent());
        var reload = throttle(()->reload(api, behavior, params, sortBy, sort), 100);
        Consumer<Object> update = evt->reload.run();
        params.subscribe(update::accept);
        sortBy.subscribe(update::accept);
        sort.subscribe(update::accept);
        if(behavior.getValue().length == 0) reload.run();
        return behavior;
    }
    @Provides @Singleton BehaviorSubject<Set<String>> params() {
        return behavior(new HashSet<>());
    }
    @Provides @Singleton @Named("sort-by") BehaviorSubject<String> sortBy() {
        return behavior("publishedAt");
    }
    @Provides @Singleton @Named("sort") BehaviorSubject<String> sort() {
        return behavior("false");
    }
    @Provides @Singleton HTMLContainerBuilder<HTMLElement> postCardContainer() {
        return htmlContainer("sac-post-card-container", HTMLElement.class);
    }
    private CatalogItem[] getCatalogItemIfPresent() {
        var contents = DomGlobal.document.getElementById("article-contents");
        if(contents==null) return new CatalogItem[0];
        contents.remove();
        return contents.getElementsByTagName("div").asList().stream()
                .filter(s->s.parentElement==contents)
                .map(e->(HTMLElement) Js.cast(e)).map(this::map)
                .toArray(CatalogItem[]::new);
    }
    private CatalogItem map(HTMLElement elem) {
        var thumbnail = elem.querySelector("#thumbnail").getAttribute("src");
        var title = elem.querySelector("#title").innerHTML;
        var description = elem.querySelector("#description").innerHTML;
        var author = elem.querySelector("#author").innerHTML.replace("written by ", "");
        var updateAt = -1L;
        try { updateAt = Long.parseLong(elem.querySelector("#update-date").innerHTML.replace("posted in ", "")); } catch(Exception ignore) { }
        var tags = elem.querySelector("#tags").getElementsByTagName("md-assist-chip").asList().stream()
                .map(i->i.getAttribute("label")).toArray(String[]::new);
        return new CatalogItem().title(title).description(description).thumbnail(thumbnail).author(author).updatedAt(updateAt).tags(tags);
    }
    private void reload(ArticleApi api, BehaviorSubject<CatalogItem[]> articles, BehaviorSubject<Set<String>> params, BehaviorSubject<String> sortBy, BehaviorSubject<String> sort) {
        api.search(0, 10, sortBy.getValue(), Boolean.parseBoolean(sort.getValue()), params.getValue().stream().toArray(String[]::new)).then(item->{
            articles.next(item);
            return null;
        }).catch_(err->{
            DomGlobal.console.error(Global.JSON.stringify(err));
            return null;
        });
    }
}

