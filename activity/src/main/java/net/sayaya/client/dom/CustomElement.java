package net.sayaya.client.dom;

import elemental2.dom.*;

public class CustomElement extends HTMLElement {
    CustomElement connectedCallback() {
        return this;
    }
    CustomElement detachedCallback() {
        return this;
    }
}
