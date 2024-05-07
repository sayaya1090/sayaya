package net.sayaya.client.event;

import elemental2.dom.DomGlobal;
import elemental2.dom.MessageEvent;
import net.sayaya.client.data.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;
import java.util.logging.Logger;

import static elemental2.core.Global.JSON;

@Singleton
public class ModuleEventListener {
    private static final Logger logger = Logger.getLogger(ModuleEventListener.class.getName());
    private final Set<ModuleEventHandler> eventHandlers;
    @Inject ModuleEventListener(Set<ModuleEventHandler> handlers) {
        this.eventHandlers = handlers;
    }
    public void listen() {
        DomGlobal.window.addEventListener("message", evt2->{
            MessageEvent<String> evt = (MessageEvent<String>) evt2;
            String json = evt.data;
            if(json == null || json.isEmpty()) return;
            Message msg = (Message) JSON.parse(json);
            evt2.stopPropagation();
            evt2.preventDefault();
            for(var handler: eventHandlers) if(handler.chk(msg.type)) {
                handler.exec(msg);
                break;
            }
        });
    }
}
