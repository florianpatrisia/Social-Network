package com.example.laborator.utils.observer;
import com.example.laborator.utils.events.Event;
import com.example.laborator.utils.events.UtilizatorChangeEventType;


public interface Observer<E extends Event> {
    void update(E e);
}