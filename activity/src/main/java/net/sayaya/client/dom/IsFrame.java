package net.sayaya.client.dom;

import net.sayaya.client.data.Progress;
import net.sayaya.rx.subject.BehaviorSubjectJs;
import org.jboss.elemento.Attachable;

public interface IsFrame extends Attachable {
   void url(BehaviorSubjectJs<String> subject);
   void progress(Progress progress);
}