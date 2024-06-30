package net.sayaya.client.list;

import dagger.Provides;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.rx.subject.BehaviorSubject;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Singleton;
import java.util.Set;

import static net.sayaya.rx.subject.BehaviorSubject.behavior;
import static org.jboss.elemento.Elements.htmlContainer;

@dagger.Module
public class ArticleListModule {
    @Provides @Singleton BehaviorSubject<CatalogItem[]> articles() {
        BehaviorSubject<CatalogItem[]> behavior = behavior(getCatalogItemIfPresent());
        //var reload = throttle(()->reload(api, behavior, sortBy, sort), 100);
        //Consumer<Object> update = evt->reload.run();
        //sortBy.subscribe(update::accept);
        //sort.subscribe(update::accept);
        //reload.run();
        return behavior;
    }
    @Provides @Singleton static BehaviorSubject<Set<String>> params() {
        BehaviorSubject<Set<String>> behavior = behavior(null);
        return behavior;
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
}

