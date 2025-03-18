package com.example.event_manager.events.domain.scheduler;

import com.example.event_manager.events.EventStatus;
import com.example.event_manager.events.domain.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Component
@EnableScheduling
public class EventStatusScheduler {

    private final EventService eventService;

    public EventStatusScheduler(
            EventService eventService
    ) {
        this.eventService = eventService;
    }

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void scheduleEvent() {
        var eventList = eventService.getAllEvents();
        var now = ZonedDateTime.now();

        eventList.forEach(event -> {
            var eventEndTime = event.date().plusHours(event.duration());

            if (now.isBefore(event.date())) {

                eventService.updateStatus(event.id(), EventStatus.WAIT_START);

            } else if (now.isAfter(event.date()) && now.isBefore(eventEndTime)) {

                eventService.updateStatus(event.id(), EventStatus.STARTED);

            } else if (now.isAfter(eventEndTime)) {

                eventService.updateStatus(event.id(), EventStatus.FINISHED);

            }
        });
    }

    //Метод для получения реального времени(для тестирования)
    @Scheduled(fixedRate = 10000)
    public void getTestTime() {
        System.out.println(ZonedDateTime.now());
    }
}
