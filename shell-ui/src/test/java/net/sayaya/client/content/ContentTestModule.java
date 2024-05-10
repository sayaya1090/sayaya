package net.sayaya.client.content;

import dagger.Provides;
import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.RequestInit;
import elemental2.dom.Response;
import elemental2.dom.ResponseInit;
import elemental2.promise.Promise;
import net.sayaya.client.api.FetchApi;
import net.sayaya.client.data.Menu;
import net.sayaya.client.data.Page;
import net.sayaya.rx.subject.BehaviorSubject;

import javax.inject.Named;
import javax.inject.Singleton;

import static elemental2.core.Global.JSON;
import static net.sayaya.rx.subject.BehaviorSubject.behavior;

@dagger.Module
public class ContentTestModule {
    @Provides @Singleton @Named("contentUrl") static BehaviorSubject<String> provideContentUrl() {
        var content = DomGlobal.document.getElementById("content");
        return behavior(content.getAttribute("src"));
    }
    @Provides @Singleton static FetchApi fetchApi() {
        return new FetchApi() {
            @Override public Promise<Response> request(String url, RequestInit param) {
                url = Global.decodeURI(url);
                if(url.startsWith("/shell/menu")) return Promise.resolve(menu(url, param));
                else return Promise.resolve(Response.error());
            }
        };
    }
    public static Response menu(String url, RequestInit param) {
        var headers = ResponseInit.create();
        headers.setHeaders(new String[][] {
                new String[] {"status", "200"}
        });;
        var menu1 = new Menu(); {
            menu1.title = "Menu 1";
            menu1.supportingText = "Supporting text 1";
            menu1.trailingText = "Trailing text 1";
            menu1.order = "A";
            menu1.icon = "fa-user";
            menu1.iconType = "sharp";
            var page1 = new Page(); {
                page1.title = "menu1-page1";
                page1.uri = "menu1-page1";
                page1.order = "AA";
                page1.icon = "fa-user";
                page1.iconType = "sharp";
            }
            var page2 = new Page(); {
                page2.title = "menu1-page2";
                page2.uri = "menu1-page2";
                page2.order = "AB";
                page2.icon = "fa-user";
                page2.iconType = "sharp";
            }
            menu1.children = new Page[] { page1, page2 };
        }
        var menu2 = new Menu(); {
            menu2.title = "Menu 2";
            menu2.supportingText = "Supporting text 2";
            menu2.trailingText = "Trailing text 2";
            menu2.order = "B";
            menu2.icon = "fa-user";
            menu2.iconType = "sharp";
            var page1 = new Page(); {
                page1.title = "menu2-page1";
                page1.uri = "menu2-page1";
                page1.order = "BA";
                page1.icon = "fa-user";
                page1.iconType = "sharp";
            }
            menu2.children = new Page[] { page1 };
        }
        var menu3 = new Menu(); {
            menu3.title = "Menu 3";
            menu3.order = "1";
            menu3.icon = "fa-left-from-bracket";
            menu3.iconType = "sharp";
            menu3.bottom = true;
            var page1 = new Page(); {
                page1.title = "menu3-page1";
                page1.uri = "menu3-page1";
                page1.order = "1A";
                page1.icon = "fa-user";
                page1.iconType = "sharp";
            }
            menu3.children = new Page[] { page1 };
        }
        var menu4 = new Menu(); {
            menu4.title = "Menu 4";
            menu4.order = "0";
            menu4.icon = "fa-right-to-bracket";
            menu4.iconType = "sharp";
            menu4.bottom = true;
            var page1 = new Page(); {
                page1.title = "menu4-page1";
                page1.uri = "menu4-page1";
                page1.order = "0A";
                page1.icon = "fa-user";
                page1.iconType = "sharp";
            }
            menu4.children = new Page[] { page1 };
        }
        return new Response(JSON.stringify(new Menu[] { menu1, menu2, menu3, menu4 }), headers);
    }
}
