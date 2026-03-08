package com.example.demo.route.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse implements Serializable {

    private String routeLabel;

    private LocalDate travelDate;

    private List<RouteSegmentResponse> segments;

    private Integer numberOfTransportations;

    private String description;
}
