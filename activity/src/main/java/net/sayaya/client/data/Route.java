package net.sayaya.client.data;

import jsinterop.annotations.*;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
public final class Route extends Message {
	public String url;
	@JsProperty(name="route_type")
	public RouteType param;
	@JsProperty(name="should_replace")
	public boolean shouldReplace;
	@JsOverlay
	@JsIgnore
	public static RouteBuilder builder() {
		return new RouteBuilder();
	}

	@Setter
	@Accessors(fluent=true)
	public static class RouteBuilder {
		private String url;
		private RouteType type;
		private boolean shouldReplace;
		RouteBuilder() {}
		public Route build() {
			var route = new Route();
			route.type = "route";
			route.url = url;
			route.param = type;
			route.shouldReplace = shouldReplace;
			return route;
		}
	}
	public enum RouteType {
		FRAME, HOST
	}
}
