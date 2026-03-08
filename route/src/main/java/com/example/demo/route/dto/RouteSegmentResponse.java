package com.example.demo.route.dto;

import com.example.demo.location.dto.LocationResponse;
import com.example.demo.transportation.TransportationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteSegmentResponse implements Serializable {

    private Long transportationId;

    private LocationResponse origin;

    private LocationResponse destination;

    private TransportationType transportationType;

    private String segmentLabel;

}
