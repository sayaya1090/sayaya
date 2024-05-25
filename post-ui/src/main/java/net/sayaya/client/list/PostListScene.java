package net.sayaya.client.list;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.ViewCSS;
import jsinterop.base.Js;
import net.sayaya.client.component.CardContainerElementBuilder;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.client.util.Debounce;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.elements.MenuElementBuilder;
import net.sayaya.ui.elements.SelectElementBuilder;
import net.sayaya.ui.elements.TextFieldElementBuilder;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import java.util.stream.IntStream;

import static elemental2.dom.DomGlobal.requestAnimationFrame;
import static net.sayaya.client.util.Debounce.debounce;
import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static net.sayaya.ui.elements.SelectElementBuilder.select;
import static net.sayaya.ui.elements.TextFieldElementBuilder.textField;
import static org.jboss.elemento.Elements.div;

@Singleton
public class PostListScene extends HTMLContainerBuilder<HTMLDivElement> {
    private final TextFieldElementBuilder.OutlinedTextFieldElementBuilder iptSearchBox = textField().outlined().label("Search");
    private final SelectElementBuilder.OutlinedSelectElementBuilder iptOrderBy = select().outlined().label("Sort by")
            .option().value("title").headline("Title").end()
            .option().value("createdAt").headline("Created").end()
            .option().value("updatedAt").headline("Updated").end()
            .required(true);
    private final SelectElementBuilder.OutlinedSelectElementBuilder iptOrder = select().outlined().label("")
            .option().value("true").headline("Asc").end()
            .option().value("false").headline("Desc").end()
            .required(true);
    private final CardContainerElementBuilder container;
    private final MenuElementBuilder.TopMenuElementBuilder menu = MenuElementBuilder.menu().positioning(MenuElementBuilder.Position.Fixed);
    @Inject public PostListScene(
            CardContainerElementBuilder container,
            @Named("sort-by") BehaviorSubject<String> sortBy,
            @Named("sort") BehaviorSubject<String> sort) {
        super(div().element());
        this.container = container;
        this.style("display: flex; flex-direction: column; height: 100%;")
                .add(div().style("padding: 0.5rem; display: flex; justify-content: space-between; align-items: center; gap: 0.5rem;")
                        .add(iptSearchBox.style("flex-grow: 1;")).add(iptOrderBy).add(iptOrder))
                .add(container)
                .add(menu);
        sortBy.subscribe(v->iptOrderBy.element().value = v);
        sort.subscribe(v->iptOrder.element().value = v);
        iptOrderBy.onChange(evt->sortBy.next(iptOrderBy.value()));
        iptOrder.onChange(evt->sort.next(iptOrder.value()));
        //posts.subscribe(this::layout);
        menu.item().start(icon().css("fa-sharp", "fa-light", "fa-pen-to-square").style("font-size: 1rem;")).headline("Edit").on(EventType.click, evt->{
                    evt.preventDefault();
                    //RouteApi.route(selected.url, true, false);
                }).end()
                .item().start(icon().css("fa-sharp", "fa-light", "fa-up-to-line").style("font-size: 1rem;")).headline("Publish").on(EventType.click, evt->{
                    evt.preventDefault();
                    //RouteApi.route(selected.url + "/publish", true, false);
                }).end()
                .item().start(icon().css("fa-sharp", "fa-light", "fa-chart-line").style("font-size: 1rem;")).headline("Statistics").on(EventType.click, evt->{
                    evt.preventDefault();
                }).end();
       // allocate();
        //DomGlobal.window.addEventListener(EventType.resize.name, evt->allocate());

        container.element().add(
                IntStream.range(0, 30).mapToObj(i->new CatalogItem().title("천하무적 이런 건 못 참지")
                                .tags(new String[] {"webflux", "kotlin", "spring"}).author("아인슈타인")
                                .description("고1 봄, 친구들과 매점을 가는 중이었는데 목련나무 앞에서 처음 본 여자 선생님이 \"지금 떨어지는 목련이 참 예쁘고, 너희도 참 예쁘다. 내가 너희를 사진 찍어줘도 될까?"))
                        .toArray(CatalogItem[]::new)
        );
    }
    private static final double REM = Global.parseFloat(Js.<ViewCSS>cast(DomGlobal.window).getComputedStyle(DomGlobal.document.documentElement).fontSize);
    private static final double TRACK_WIDTH_MIN = 15*REM;
    private final Debounce allocate = debounce(() -> {
        /*var clientWidth = container.element().clientWidth;
        var trackCnt = (int)(clientWidth/TRACK_WIDTH_MIN);
        var trackWidth = TRACK_WIDTH_MIN+(clientWidth-trackCnt*TRACK_WIDTH_MIN)/trackCnt;


        container.element().innerHTML = "";
        for(int i = 0; i < trackCnt; ++i) {
            var track = div();

        }*/
    }, 300);
    private void allocate() {
        requestAnimationFrame(evt-> allocate.run());
    }
}
