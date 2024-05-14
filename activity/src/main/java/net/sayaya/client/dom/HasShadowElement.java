package net.sayaya.client.dom;

import elemental2.dom.*;
import org.gwtproject.safehtml.shared.SafeHtml;
import org.jboss.elemento.*;

public interface HasShadowElement<E extends Element, B extends TypedBuilder<E, B>> extends HasElement<E, B> {
    ShadowRoot shadow();
    default B textContent(String text) {
        this.shadow().textContent = text;
        return this.that();
    }
    default B textNode(String text) {
        Node textNode = firstTextNode(this.shadow());
        if (textNode != null) {
            textNode.nodeValue = text;
        } else {
            this.add(text);
        }

        return this.that();
    }
    private static Node firstTextNode(ShadowRoot element) {
        NodeList<Node> nodes = element.childNodes;
        for(int i = 0; i < nodes.length; ++i) {
            Node node = nodes.getAt(i);
            if (node.nodeType == Node.TEXT_NODE) {
                return node;
            }
        }
        return null;
    }
    default B add(String text) {
        this.shadow().appendChild(this.element().ownerDocument.createTextNode(text));
        return this.that();
    }

    default B innerHtml(SafeHtml html) {
        this.shadow().innerHTML = html.asString();
        return this.that();
    }
}