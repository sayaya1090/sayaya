package net.sayaya.client.content.component;

import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import net.sayaya.client.api.ShellApi;
import net.sayaya.client.data.Menu;
import net.sayaya.client.data.Page;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.rx.subject.BehaviorSubjectJs;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.nullsLast;

@Singleton
public class MenuManager extends BehaviorSubject<List<Menu>> {
    private final ShellApi shellApi;
    private final BehaviorSubject<Page> page;
    private final Map<Page, Menu> menuByPage = new HashMap<>();
    @Inject MenuManager(ShellApi shellApi, BehaviorSubject<Page> page, @Named("url") BehaviorSubjectJs<String> url) {
        super(new LinkedList<>());
        this.shellApi = shellApi;
        this.page = page;
        url.subscribe(this::reload);
    }
    public Promise<Void> reload(String url) {
        return shellApi.findMenu().then(m -> {
            m = m.stream().sorted(Comparator.comparing(i->i.order, nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
            next(m);
            menuByPage.clear();
            for(Menu menu : m) for(Page p: menu.children) menuByPage.put(p, menu);
            var nextPage = (url!=null && url.length()>2) ? pickByUrl(m, url) : m.get(0).children[0];
            if(page.getValue()==null || !page.getValue().uri.equals(nextPage.uri)) page.next(nextPage);
            return null;
        });
    }
    private Page pickByUrl(List<Menu> menu, String url) {
        return menu.stream()
                .flatMap(i-> Arrays.stream(i.children))
                .filter(p->p.uri.startsWith(url))
                .max(Comparator.comparing(p-> p.uri.startsWith(url) ? p.uri.length() : -1))
                .orElse(null);
    }
    public Menu parentOf(Page page) { return menuByPage.get(page); }
}
