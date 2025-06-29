package com.example.event_manager.events.eventKafka;

public class EventFieldChangeUtil {

    private EventFieldChangeUtil() {

    }

    public static <T> EventFieldChange<T> of(T oldValue, T newValue) {
        EventFieldChange<T> change = new EventFieldChange<>();
        change.setOldField(oldValue);
        change.setNewField(newValue);
        return change;
    }
}
