package net.sayaya.client;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.dom.CardElement;
import net.sayaya.client.dom.CustomElements;

public class Card implements EntryPoint {
    @Override
    public void onModuleLoad() {
        CustomElements.define("sac-post-card", CardElement.class, CardElement::initialize);
    }
}
