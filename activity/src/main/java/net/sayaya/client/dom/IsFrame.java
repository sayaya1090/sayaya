package net.sayaya.client.dom;

import net.sayaya.client.data.Progress;
import net.sayaya.rx.subject.BehaviorSubjectJs;
import org.jboss.elemento.Attachable;

public interface IsFrame extends Attachable {
   void onHashChange(String hash);
   default void urlSubject(BehaviorSubjectJs<String> subject) {}
   default void progressSubject(BehaviorSubjectJs<Progress> subject) {}
}