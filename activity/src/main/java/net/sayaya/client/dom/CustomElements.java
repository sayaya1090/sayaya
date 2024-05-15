package net.sayaya.client.dom;

import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import jsinterop.annotations.*;
import jsinterop.base.Js;
import jsinterop.base.JsConstructorFn;
import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

import static elemental2.dom.DomGlobal.customElements;
import static jsinterop.base.Js.asConstructorFn;

@UtilityClass
public class CustomElements {
    public <T extends CustomElement> void define(String tagName, Class<T> webComponentType, Consumer<T> initializer) {
        Type<CustomElement> htmlElementConstructor = Js.uncheckedCast(Js.asPropertyMap(DomGlobal.window).get("HTMLElement"));
        Type<T> type = Type.subtypeOf(htmlElementConstructor, asConstructorFn(webComponentType), initializer);
        customElements.define(tagName, type.asClass());
    }
    @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
    public static class Type<T extends CustomElement> {
        @JsFunction
        public interface Constructor {
            Object construct();
        }
        public Prototype prototype;
        public Object __proto__;
        @JsOverlay
        public static <T extends CustomElement, U extends T> Type<U> subtypeOf(Type<T> superclass, JsConstructorFn<U> realClass, Consumer<U> initializer) {
            Type<U> realType = Js.cast(realClass);
            Constructor syntheticCtor = new Constructor() {
                @Override
                public Object construct() {
                    U instance = Js.cast(Reflect.construct(superclass, new JsArray<>(), this));
                    initializer.accept(instance);
                    return instance;
                }
            };
            realType.prototype.__proto__ = Js.<Type<T>>cast(superclass).prototype;
            realType.__proto__ = superclass;
            Type<U> shimType = Js.cast(syntheticCtor);
            shimType.prototype.__proto__ = realType.prototype;
            shimType.__proto__ = realType;
            return shimType;
        }
        @JsOverlay
        public final JsConstructorFn<? extends T> asClass() {
            return Js.cast(this);
        }
    }
    @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
    public static class Prototype {
        public Object __proto__;
    }
    @JsType(isNative = true, namespace = JsPackage.GLOBAL)
    public static class Reflect {
        public static native Object construct(Object superClass, JsArray<?> args, Object target);
    }
    public static <E extends CustomElement> ContainerBuilder<E> customContainer(String element, Class<E> type) {
        return new ContainerBuilder<>(createCapsuledCustomElement(element, type));
    }
    public static <E extends CustomElement> E createCapsuledCustomElement(String element, Class<E> type) {
        return Js.cast(DomGlobal.document.createElement(element));
    }
}
