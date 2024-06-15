package net.sayaya.client.edit;

import elemental2.promise.Promise;
import lombok.experimental.Delegate;
import net.sayaya.client.data.Commit;
import net.sayaya.client.data.PostRequest;
import net.sayaya.rx.subject.BehaviorSubject;
import net.sayaya.ui.dom.MdDialogElement;
import net.sayaya.ui.elements.DialogElementBuilder;
import net.sayaya.ui.elements.ButtonElementBuilder.TextButtonElementBuilder;
import org.jboss.elemento.InputType;
import org.jboss.elemento.IsElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import static net.sayaya.ui.elements.ButtonElementBuilder.button;
import static net.sayaya.ui.elements.DialogElementBuilder.dialog;
import static net.sayaya.ui.elements.TextFieldElementBuilder.textField;
import static org.jboss.elemento.Elements.form;

@Singleton
public class CommitDialog implements IsElement<MdDialogElement> {
    @Delegate private final DialogElementBuilder _this = dialog().ariaLabel("GitHub Commit Configuration");
    private final TextButtonElementBuilder btnCancel = button().text().add("CLOSE").icon("close");
    private final TextButtonElementBuilder btnApply = button().text().add("SAVE").icon("save");

    @Inject CommitDialog(BehaviorSubject<Commit> commit) {
        var iptCommitMessage = textField().outlined().type(InputType.textarea).label("Commit Message").required(true);
        _this.style("max-width: calc(50em + 48px);")
                .headline("GitHub Commit")
                .content(form().add(iptCommitMessage.style("padding-left: 1rem; padding-right: 1rem; width: -webkit-fill-available; height: 6rem; width: 39em;")))
                .actions(form().add(btnCancel).add(btnApply));
        iptCommitMessage.value(commit.getValue()!=null?commit.getValue().msg:"Commit message");
        btnCancel.onClick(evt->{
            evt.preventDefault();
            _this.close();
        });
        btnApply.onClick(evt->{
            evt.preventDefault();
            commit.getValue().msg = iptCommitMessage.value();
            _this.close();
        });
    }
    public Promise<PostRequest> open(PostRequest post) {
        Promise<PostRequest> promise = new Promise<>((resolve, reject)->{
            btnCancel.onClick(evt->reject.onInvoke("Cancel"));
            btnApply.onClick(evt->resolve.onInvoke(post));
        });
        this.open();
        return promise;
    }
}
