package com.example.event_manager.Locations;

import com.example.event_manager.Events.EventEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "locations")
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String address;
    private Integer capacity;
    private String description;
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<EventEntity> events;
    public List<EventEntity> getEvents() {
        return events;
    }

    public void setEvents(List<EventEntity> events) {
        this.events = events;
    }

    public LocationEntity() {
    }

    public LocationEntity(Integer id, String address, Integer capacity, String description, List<EventEntity> events) {
        this.id = id;
        this.address = address;
        this.capacity = capacity;
        this.description = description;
        this.events = events;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
