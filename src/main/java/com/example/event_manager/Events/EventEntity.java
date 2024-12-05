package com.example.event_manager.Events;

import com.example.event_manager.Locations.LocationEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "events")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer ownerId;
    private Integer maxPlaces;
    private Integer occupiedPlaces;
    private String date;
    private Integer duration;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private LocationEntity location;  // Сменили тип на LocationEntity

    public EventEntity() {
    }

    public EventEntity(
            Integer id,
            String name,
            Integer ownerId,
            Integer maxPlaces,
            Integer occupiedPlaces,
            String date,
            Integer duration,
            LocationEntity location  // Меняем параметр на LocationEntity
    ) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.maxPlaces = maxPlaces;
        this.occupiedPlaces = occupiedPlaces;
        this.date = date;
        this.duration = duration;
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(Integer maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public Integer getOccupiedPlaces() {
        return occupiedPlaces;
    }

    public void setOccupiedPlaces(Integer occupiedPlaces) {
        this.occupiedPlaces = occupiedPlaces;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LocationEntity getLocation() {  // Теперь возвращаем объект LocationEntity
        return location;
    }

    public void setLocation(LocationEntity location) {  // Сеттер тоже с LocationEntity
        this.location = location;
    }

    public Integer getLocationId() {  // Если нужно получить только ID локации
        return location != null ? location.getId() : null;
    }
}
