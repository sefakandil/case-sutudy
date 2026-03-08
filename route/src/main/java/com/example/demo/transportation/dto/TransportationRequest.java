package com.example.demo.transportation.dto;

import com.example.demo.transportation.DayOfWeek;
import com.example.demo.transportation.TransportationType;
import com.example.demo.location.dto.LocationResponse;
import lombok.Data;

import java.util.List;

@Data
public class TransportationRequest {

    private LocationResponse origin;

    private LocationResponse destination;

    private TransportationType transportationType;

    private List<DayOfWeek> operatingDays;
}
