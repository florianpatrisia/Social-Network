package com.example.laborator.utils.events;

import com.example.laborator.domain.Message;
import com.example.laborator.domain.Utilizator;

public class MessageChangeEventType implements Event{
    private ChangeEventType type;
    private Message data, oldData;

    public MessageChangeEventType(ChangeEventType type, Message data) {
        this.type = type;
        this.data = data;
    }

    public MessageChangeEventType(ChangeEventType type, Message data, Message oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }
}
