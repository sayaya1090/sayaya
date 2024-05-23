package net.sayaya.client;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.component.CardElementBuilder;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.client.dom.CardElement;
import net.sayaya.client.dom.CustomElements;

import static org.jboss.elemento.Elements.body;
import static org.jboss.elemento.Elements.div;

public class CardTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        CustomElements.define("sac-post-card", CardElement.class,CardElement::initialize);
        var container = div().style("display: grid; grid-template-columns: repeat(auto-fit, minmax(19rem, 1fr)); gap: 1rem; padding: 0.5rem; justify-content: space-between; overflow-x: hidden; overflow-y: auto;");
        body().add(container);
        for(int i = 0; i < 30; ++i) {
            var card = new CardElementBuilder();
            container.add(card);
            var item = new CatalogItem().title("천하무적 이런 건 못 참지")
                    .tags(new String[] {"webflux", "kotlin", "spring"}).author("아인슈타인")
                    .description("고1 봄, 친구들과 매점을 가는 중이었는데 목련나무 앞에서 처음 본 여자 선생님이 \"지금 떨어지는 목련이 참 예쁘고, 너희도 참 예쁘다. 내가 너희를 사진 찍어줘도 될까?");
            card.element().item(item);
        }
    }
}
