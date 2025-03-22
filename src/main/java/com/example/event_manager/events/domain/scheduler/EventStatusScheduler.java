package com.example.event_manager.events.domain.scheduler;

import com.example.event_manager.events.EventStatus;
import com.example.event_manager.events.domain.EventService;
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

    @Scheduled(cron = "${event.stats.cron}")
    @Transactional
    public void scheduleEvent() {
        var eventList = eventService.getAllEvents();
        var now = ZonedDateTime.now();

        eventList.forEach(event -> {

            var eventEndTime = event.date().plusMinutes(event.duration());

            System.out.println("Время окончания мероприятия: " + eventEndTime);

            if (now.isBefore(event.date())) {

                eventService.updateStatus(event.id(), EventStatus.WAIT_START.name());

            } else if (now.isAfter(event.date()) && now.isBefore(eventEndTime)) {

                eventService.updateStatus(event.id(), EventStatus.STARTED.name());

            } else if (now.isAfter(eventEndTime)) {

                eventService.updateStatus(event.id(), EventStatus.FINISHED.name());

            }
        });
    }

    //Метод для получения реального времени(для тестирования)
    @Scheduled(cron = "${event.stats.cron}")
    public void getTestTime() {
        System.out.println(ZonedDateTime.now());
    }
}
