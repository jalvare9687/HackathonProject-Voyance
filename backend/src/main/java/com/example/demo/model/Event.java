package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "events")
public class Event {
    @Id
    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "impact_multiplier")
    private Double impactMultiplier;
}
