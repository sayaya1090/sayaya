package net.sayaya.client.event;

import net.sayaya.client.data.Message;

interface ModuleEventHandler {
    boolean chk(String type);
    void exec(Message msg);
}