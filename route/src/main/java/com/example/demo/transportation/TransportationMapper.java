package com.example.demo.transportation;

import com.example.demo.location.Location;
import com.example.demo.location.dto.LocationResponse;
import com.example.demo.transportation.dto.TransportationRequest;
import com.example.demo.transportation.dto.TransportationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransportationMapper {

      @Mapping(target = "id", ignore = true)
    @Mapping(target = "createUser", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateUser", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "visible", ignore = true)
    @Mapping(target = "origin", source = "origin")
    @Mapping(target = "destination", source = "destination")
    @Mapping(target = "transportationType", source = "request.transportationType")
    @Mapping(target = "operatingDaysMask", expression = "java(convertDayOfWeekListToIntegerSet(request.getOperatingDays()))")
    Transportation convertToTransportation(TransportationRequest request, LocationResponse origin, LocationResponse destination);

    default int convertDayOfWeekListToIntegerSet(List<DayOfWeek> dayOfWeeks) {
        if (dayOfWeeks == null || dayOfWeeks.isEmpty()) {
            return 0;
        }
        final Set<Integer> dayList = dayOfWeeks.stream()
                .map(DayOfWeek::getValue)
                .collect(Collectors.toSet());

        int mask = 0;
        for (int day : dayList) {
            mask |= (1 << (day - 1));
        }

        return mask;
    }

    @Mapping(target = "operatingDays", expression = "java(convertIntToDayOfWeeks(entity.getOperatingDaysMask()))")
    TransportationResponse convertToTransportationResponse(Transportation entity);

    default List<DayOfWeek> convertIntToDayOfWeeks(int mask) {

        final Set<Integer> days = new HashSet<>();

        for (int day = 1; day <= 7; day++) {
            final int bit = 1 << (day - 1);
            if ((mask & bit) != 0) {
                days.add(day);
            }
        }
        return days.stream().map(DayOfWeek::fromValue).toList();
    }

    List<TransportationResponse> convertToTransportationResponseList(List<Transportation> transportations);
}
