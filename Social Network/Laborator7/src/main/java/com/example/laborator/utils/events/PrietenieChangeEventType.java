package com.example.laborator.utils.events;

import com.example.laborator.domain.Prietenie;

public class PrietenieChangeEventType implements Event{
    private ChangeEventType type;
    private Prietenie data, oldData;

    public PrietenieChangeEventType(ChangeEventType type, Prietenie data) {
        this.type = type;
        this.data = data;
    }

    public PrietenieChangeEventType(ChangeEventType type, Prietenie data, Prietenie oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }
    public Prietenie getData() {
        return data;
    }
    public Prietenie getOldData() {
        return oldData;
    }
}
