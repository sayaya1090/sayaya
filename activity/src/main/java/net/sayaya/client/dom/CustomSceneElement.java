package net.sayaya.client.dom;

public abstract class CustomSceneElement<E extends CustomSceneElement<E>> extends CustomElement {
   abstract E draw();
}
