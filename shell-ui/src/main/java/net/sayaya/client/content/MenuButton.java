package net.sayaya.client.content;

import elemental2.dom.Element;
import lombok.experimental.Delegate;
import net.sayaya.ui.dom.MdIconButtonElement;
import net.sayaya.ui.elements.IconButtonElementBuilder;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static net.sayaya.ui.svg.SvgBuilder.svg;
import static net.sayaya.ui.svg.SvgPathBuilder.path;

public class MenuButton implements  IconButtonElementBuilder<MdIconButtonElement, IconButtonElementBuilder.PlainIconButtonElementBuilder> {
    static MenuButton menu(MenuManager menuManager) {
        return new MenuButton(menuManager);
    }
    private final Element icon = svg().viewBox(0, 0, 100, 100)
            .add(path().css("line").d("M 80,29.000046 H 20.000231 C 20.000231,29.000046 5.501161,28.817352 5.467013,66.711331 5.456858,77.980673 9.033919,81.670246 14.740827,81.668997 20.447739,81.667751 25.000211,74.999942 25.000211,74.999942 L 75.000021,25.000058"))
            .add(path().css("line", "line-middle").d("M 80,50 H 20"))
            .add(path().css("line").d("M 80,70.999954 H 20.000231 C 20.000231,70.999954 5.501161,71.182648 5.467013,33.288669 5.456858,22.019327 9.033919,18.329754 14.740827,18.331003 20.447739,18.332249 25.000211,25.000058 25.000211,25.000058 L 75.000021,74.999942"))
            .element();
    @Delegate private final IconButtonElementBuilder.PlainIconButtonElementBuilder btnToggleMenu = button().icon().add(icon).css("menu-button").ariaLabel("Menu");
    private MenuButton(MenuManager menuManager) {
        menuManager.onStateChange(state-> {
            switch (state) {
                case SHOW:
                    icon.classList.add("opened");
                    break;
                case HIDE:
                    icon.classList.remove("opened");
                    break;
            }
        });
    }
}