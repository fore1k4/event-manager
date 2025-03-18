package com.example.event_manager.events.database;

import com.example.event_manager.events.EventStatus;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "events")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "max_places", nullable = false)
    private Long places;

    @Column(name = "occupied_places")
    private  Long occupiedPlaces;

    @OneToMany(mappedBy = "event")
    private List<EventRegistrationEntity> registrationList;

    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @Column(name = "cost", nullable = false)
    private Long cost;

    @Column(name = "duration", nullable = false)
    private Long duration;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(name = "status", nullable = false)
    private EventStatus status;

    public EventEntity() {
    }

    public EventEntity(
            Long id,
            String name,
            Long ownerId,
            Long places,
            Long occupiedPlaces,
            List<EventRegistrationEntity> registrationList,
            ZonedDateTime date,
            Long cost,
            Long duration,
            Long locationId,
            EventStatus status
    ) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.places = places;
        this.occupiedPlaces = occupiedPlaces;
        this.registrationList = registrationList;
        this.date = date;
        this.cost = cost;
        this.duration = duration;
        this.locationId = locationId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getPlaces() {
        return places;
    }

    public void setPlaces(Long maxPlaces) {
        this.places = maxPlaces;
    }

    public List<EventRegistrationEntity> getRegistrationList() {
        return registrationList;
    }

    public void setRegistrationList(List<EventRegistrationEntity> registrationList) {
        this.registrationList = registrationList;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Long getOccupiedPlaces() {
        return occupiedPlaces;
    }

    public void setOccupiedPlaces(Long occupiedPlaces) {
        this.occupiedPlaces = occupiedPlaces;
    }
}
