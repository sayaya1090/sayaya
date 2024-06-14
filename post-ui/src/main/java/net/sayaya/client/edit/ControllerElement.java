package net.sayaya.client.edit;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import lombok.experimental.Delegate;
import net.sayaya.client.api.PostApi;
import net.sayaya.client.data.CatalogItem;
import net.sayaya.client.data.PostRequest;
import net.sayaya.rx.subject.BehaviorSubject;
import org.jboss.elemento.HTMLContainerBuilder;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static net.sayaya.ui.elements.IconElementBuilder.icon;
import static net.sayaya.ui.svg.elements.SvgBuilder.svg;
import static net.sayaya.ui.svg.elements.SvgMaskBuilder.mask;
import static net.sayaya.ui.svg.elements.SvgPathBuilder.path;
import static net.sayaya.ui.svg.elements.SvgRectBuilder.rect;
import static org.jboss.elemento.Elements.div;

@Singleton
public class ControllerElement implements IsElement<HTMLElement> {
    @Delegate private final HTMLContainerBuilder<HTMLDivElement> elem = div();
    @Inject ControllerElement(TitleElement title, GithubSettingsElement btnGithubConfig, CommitDialog commitDialog,
                              PostApi api,
                              BehaviorSubject<PostRequest> post, BehaviorSubject<CatalogItem> catalog,
                              @Named("is-preview-mode") BehaviorSubject<Boolean> isPreviewMode,
                              @Named("is-publish-mode") BehaviorSubject<Boolean> isPublishMode) {
        var btnPreview = button().icon().add(icon().css("fa-sharp", "fa-light", "fa-eye")).toggle(icon().css("fa-sharp", "fa-light", "fa-eye-slash"), false);
        var btnPublish = button().icon()
                .add(svg().viewBox(0, 0, 640, 512)
                        .add(path().stroke("var(--_icon-color)").fill("var(--_icon-color)").transform("translate(50, 50)")
                                .d("M160 416H128 32 0V384 32 0H32 480h32V32 384v32H480 304L192 490.7 160 512V473.5 448 416zm0-32h32v32 36.2l94.2-62.8 8.1-5.4H304 480V32H32V384H160zm59.3-228.7L166.6 208l52.7 52.7L230.6 272 208 294.6l-11.3-11.3-64-64L121.4 208l11.3-11.3 64-64L208 121.4 230.6 144l-11.3 11.3zm96-22.6l64 64L390.6 208l-11.3 11.3-64 64L304 294.6 281.4 272l11.3-11.3L345.4 208l-52.7-52.7L281.4 144 304 121.4l11.3 11.3z")).element())
                .toggle(svg().viewBox(0, 0, 640, 512)
                        .add(path().stroke("var(--_icon-color)").fill("var(--_icon-color)").mask("stroke").transform("translate(50, 50)")
                                .d("M160 416H128 32 0V384 32 0H32 480h32V32 384v32H480 304L192 490.7 160 512V473.5 448 416zm0-32h32v32 36.2l94.2-62.8 8.1-5.4H304 480V32H32V384H160zm59.3-228.7L166.6 208l52.7 52.7L230.6 272 208 294.6l-11.3-11.3-64-64L121.4 208l11.3-11.3 64-64L208 121.4 230.6 144l-11.3 11.3zm96-22.6l64 64L390.6 208l-11.3 11.3-64 64L304 294.6 281.4 272l11.3-11.3L345.4 208l-52.7-52.7L281.4 144 304 121.4l11.3 11.3z"))
                        .add(path().stroke("var(--_icon-color)").fill("var(--_icon-color)").d("M27.8 8.1L40.4 18 619.5 469l12.6 9.8-19.7 25.2-12.6-9.8L20.7 43.2 8.1 33.4 27.8 8.1z"))
                        .add(mask("stroke")
                                .add(rect().fill("white").x(0).y(0).width("100%").height("100%"))
                                .add(path().stroke("black").fill("black").strokeWidth(100).d("M27.8 8.1L40.4 18 619.5 469l12.6 9.8-19.7 25.2-12.6-9.8L20.7 43.2 8.1 33.4 27.8 8.1z"))));
        var btnSave = button().icon().add(icon().css("fa-sharp", "fa-light", "fa-save"));
        this.css("controller")
            .add(title.style("flex-grow: 1; height: 3rem;"))
            .add(btnGithubConfig)
            .add(btnPreview).add(btnPublish).add(btnSave)
            .add(commitDialog);
        isPreviewMode.subscribe(preview->{
            btnPreview.element().selected = preview;
            if(preview) isPublishMode.next(false);
        });
        btnPreview.onClick(evt-> isPreviewMode.next(!btnPreview.element().selected));
        isPublishMode.subscribe(publish->{
            btnPublish.element().selected = publish;
            if(publish) isPreviewMode.next(false);
        });
        btnPublish.onClick(evt-> isPublishMode.next(!btnPublish.element().selected));
        btnSave.onClick(evt->{
            if(isPublishMode.getValue()) save(catalog.getValue());
            else save(api, post.getValue());
        });
    }
    private void save(PostApi api, PostRequest value) {
        DomGlobal.console.log("Save");
        DomGlobal.console.log(Global.JSON.stringify(value));
        Promise.resolve(value)
                .then(api::save)
                .then(id->{
                    DomGlobal.console.log("Done");
                    //RouteApi.route("/post", true, false);
                    return null;
                }).catch_(err->{
                    if("cancel".equalsIgnoreCase(err.toString())) return null;
                    else DomGlobal.console.error(Global.JSON.stringify(err));
                    return null;
                });
    }
    private void save(CatalogItem catalog) {

    }
}
