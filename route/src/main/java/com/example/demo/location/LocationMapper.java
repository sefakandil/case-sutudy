package com.example.demo.location;

import com.example.demo.location.dto.LocationRequest;
import com.example.demo.location.dto.LocationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper {

    LocationResponse convertToLocationResponse(Location data);

    Location convertToLocation(LocationRequest request);
}
