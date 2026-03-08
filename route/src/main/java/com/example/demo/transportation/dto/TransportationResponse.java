package com.example.demo.transportation.dto;

import com.example.demo.transportation.DayOfWeek;
import com.example.demo.transportation.TransportationType;
import com.example.demo.location.dto.LocationResponse;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class TransportationResponse implements Serializable {

    private Long id;  //TODO: it will be removed in the future, but for now we need it to test the API

    private LocationResponse origin;

    private LocationResponse destination;

    private TransportationType transportationType;

    private List<DayOfWeek> operatingDays;
}
