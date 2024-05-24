package net.sayaya.client;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.client.dom.CardContainerElement;
import net.sayaya.client.dom.CardElement;
import net.sayaya.client.dom.CustomElements;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.sayaya.client.component.CardContainerElementBuilder.cards;
import static org.jboss.elemento.Elements.body;

public class CardTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        CustomElements.define("sac-post-card", CardElement.class,CardElement::initialize);
        CustomElements.define("sac-post-card-container", CardContainerElement.class, CardContainerElement::initialize);
        var container = cards();
        body().add(container);
        container.add(
                IntStream.range(0, 30).mapToObj(i->new CatalogItem().title("천하무적 이런 건 못 참지")
                .tags(new String[] {"webflux", "kotlin", "spring"}).author("아인슈타인")
                .description("고1 봄, 친구들과 매점을 가는 중이었는데 목련나무 앞에서 처음 본 여자 선생님이 \"지금 떨어지는 목련이 참 예쁘고, 너희도 참 예쁘다. 내가 너희를 사진 찍어줘도 될까?"))
                .collect(Collectors.toList())
        );

    }
}
