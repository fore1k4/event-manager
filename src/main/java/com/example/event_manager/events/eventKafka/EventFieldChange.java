package com.example.event_manager.events.eventKafka;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventFieldChange<T> {
    private T oldField;
    private T newField;

    public EventFieldChange() {
        // нужен для Jackson и для вызовов new EventFieldChange<>()
    }

    public EventFieldChange(T oldField, T newField) {
        this.oldField = oldField;
        this.newField = newField;
    }

    public T getOldField() {
        return oldField;
    }

    public void setOldField(T oldField) {
        this.oldField = oldField;
    }

    public T getNewField() {
        return newField;
    }

    public void setNewField(T newField) {
        this.newField = newField;
    }

    @Override
    public String toString() {
        return "EventFieldChange{" +
                "oldField=" + oldField +
                ", newField=" + newField +
                '}';
    }
}
