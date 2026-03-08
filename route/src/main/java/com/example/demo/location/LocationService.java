package com.example.demo.location;

import com.example.demo.location.dto.LocationRequest;
import com.example.demo.location.dto.LocationResponse;

import java.util.List;
import java.util.Optional;

public interface LocationService {

    LocationResponse create(LocationRequest request);

    List<LocationResponse> getAll();

    LocationResponse getById(Long id);

    Optional<LocationResponse> findByLocationCode(String locationCode);

}
