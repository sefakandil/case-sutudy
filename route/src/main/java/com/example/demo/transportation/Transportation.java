package com.example.demo.transportation;

import com.example.demo.common.entity.BaseEntity;
import com.example.demo.location.Location;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "transportations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_transportation_origin_destination_type",
                        columnNames = {"origin_location_id", "destination_location_id", "transportation_type"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transportation extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "origin_location_id", nullable = false)
    private Location origin;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "destination_location_id", nullable = false)
    private Location destination;

    @Enumerated(EnumType.STRING)
    @Column(name = "transportation_type", nullable = false, length = 20)
    private TransportationType transportationType;

    @Column(name = "operating_days_mask", nullable = false)
    private int operatingDaysMask;

}
