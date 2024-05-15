package net.sayaya.client.content;

import com.google.gwt.core.client.EntryPoint;
import net.sayaya.client.content.dom.NavigationRailItemElement;
import net.sayaya.client.content.dom.ProgressElement;

import static net.sayaya.client.content.component.NavigationRailElementBuilder.nav;
import static net.sayaya.client.dom.CustomElements.customContainer;
import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static org.jboss.elemento.Elements.body;
import static org.jboss.elemento.Elements.div;

public class ComponentTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        ContentModule.defineCustomTags();
        progressBar();
        drawer();
    }
    private void progressBar() {
        var progress = customContainer("sac-progress", ProgressElement.class);
        body().add(progress);
        var data = new Progress();
        data.value(0, 0);
        //progress.element().update(data);
        data.intermediate();
        progress.element().update(data);
    }
    private void drawer() {
        body().add(DaggerContentTestComponent.create().drawer().style("height:-webkit-fill-available;"));
    }

    /*
     /*var menu = customContainer("sac-menu", NavigationRailElement.class).style("width: 150px; display: block;");
        var menu2 = customContainer("sac-menu", NavigationRailElement.class).style("width: 150px; display: block;");
        body().add(menu).add(menu2);
        {
            var item = customContainer("sac-menu-item", NavigationRailItemElement.class);
            menu.add(item);
            // HTMLElement ico = (item.icon!=null && item.icon.startsWith("fa-")) ? i().css("fa-sharp", "fa-light", item.icon).element():  null;
            item.add(button().icon().add(i().css("fa-sharp", "fa-light", "fa-user")).attr("slot", "menu-collapse-icon").element());

            item.element().append(icon().css("fa-sharp", "fa-light", "fa-user").attr("slot", "menu-expand-icon").element());
            item.element().append(div().add("Text").attr("slot", "headline").element());
            item.element().append(div().add("support").attr("slot", "supporting-text").element());
            var kkk = div().style("width: 100px; height: 100px; background: red; display: none;");
            var k = div().add(">").attr("slot", "trailing-supporting-text");
            k.on(EventType.mouseover, evt->{
                console.log("ff");
                kkk.style("display: block;");
            });
            item.element().append(k.element());

            item.element().expand();
        } {
            var item = customContainer("sac-menu-item", NavigationRailItemElement.class);
            menu.add(item);
            // HTMLElement ico = (item.icon!=null && item.icon.startsWith("fa-")) ? i().css("fa-sharp", "fa-light", item.icon).element():  null;
            item.add(button().icon().add(i().css("fa-sharp", "fa-light", "fa-user")).attr("slot", "menu-collapse-icon").element());

            item.element().append(icon().css("fa-sharp", "fa-light", "fa-user").attr("slot", "menu-expand-icon").element());
            item.element().append(div().add("Text").attr("slot", "headline").element());
            item.element().append(div().add("support").attr("slot", "supporting-text").element());
            item.element().append(div().add(">").attr("slot", "trailing-supporting-text").element());

            item.element().expand();
        } {
            var item = customContainer("sac-menu-item", NavigationRailItemElement.class);
            menu.add(item);
            // HTMLElement ico = (item.icon!=null && item.icon.startsWith("fa-")) ? i().css("fa-sharp", "fa-light", item.icon).element():  null;
            item.add(button().icon().add(i().css("fa-sharp", "fa-light", "fa-user")).attr("slot", "menu-collapse-icon").element());

            item.element().append(icon().css("fa-sharp", "fa-light", "fa-user").attr("slot", "menu-expand-icon").element());
            item.element().append(div().add("Text").attr("slot", "headline").element());
            item.element().append(div().add("support").attr("slot", "supporting-text").element());
            item.element().append(div().add(">").attr("slot", "trailing-supporting-text").element());

            item.element().expand();
        }
     */
}
