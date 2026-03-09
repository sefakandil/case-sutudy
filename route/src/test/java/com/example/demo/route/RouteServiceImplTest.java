package com.example.demo.route;

import com.example.demo.common.exception.ApiException;
import com.example.demo.location.LocationService;
import com.example.demo.location.dto.LocationResponse;
import com.example.demo.route.dto.RouteResponse;
import com.example.demo.route.dto.RouteSearchRequest;
import com.example.demo.transportation.TransportationService;
import com.example.demo.transportation.TransportationType;
import com.example.demo.transportation.dto.TransportationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {
    @Mock
    private LocationService locationService;
    @Mock
    private TransportationService transportationService;
    @InjectMocks
    private RouteServiceImpl routeServiceImpl;

    @Test
    void findAllValidRoutes_originNotFound() {
        RouteSearchRequest request = new RouteSearchRequest();
        request.setOriginLocationCode("IST");
        request.setDestinationLocationCode("ANK");
        request.setTravelDate(LocalDate.now());
        when(locationService.findByLocationCode("IST")).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> routeServiceImpl.findAllValidRoutes(request));
        assertThat(thrown).isInstanceOf(ApiException.class);
        assertThat(((ApiException) thrown).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(((ApiException) thrown).getMessageKey()).isEqualTo("error.location.origin.notfound");
    }

    @Test
    void findAllValidRoutes_destinationNotFound() {
        RouteSearchRequest request = new RouteSearchRequest();
        request.setOriginLocationCode("IST");
        request.setDestinationLocationCode("ANK");
        request.setTravelDate(LocalDate.now());
        LocationResponse origin = new LocationResponse();
        when(locationService.findByLocationCode("IST")).thenReturn(Optional.of(origin));
        when(locationService.findByLocationCode("ANK")).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> routeServiceImpl.findAllValidRoutes(request));
        assertThat(thrown).isInstanceOf(ApiException.class);
        assertThat(((ApiException) thrown).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(((ApiException) thrown).getMessageKey()).isEqualTo("error.location.destination.notfound");
    }

    @Test
    void findAllValidRoutes_directFlight() {
        RouteSearchRequest request = new RouteSearchRequest();
        request.setOriginLocationCode("IST");
        request.setDestinationLocationCode("ANK");
        request.setTravelDate(LocalDate.now());
        LocationResponse origin = new LocationResponse();
        origin.setId(1L);
        origin.setLocationCode("IST");
        LocationResponse dest = new LocationResponse();
        dest.setId(2L);
        dest.setLocationCode("ANK");
        when(locationService.findByLocationCode("IST")).thenReturn(Optional.of(origin));
        when(locationService.findByLocationCode("ANK")).thenReturn(Optional.of(dest));
        TransportationResponse flight = new TransportationResponse();
        flight.setOrigin(origin);
        flight.setDestination(dest);
        flight.setTransportationType(TransportationType.FLIGHT);
        flight.setId(100L);
        when(transportationService.findByOriginId(origin.getId(), request.getTravelDate().getDayOfWeek().getValue())).thenReturn(List.of(flight));
        when(transportationService.findByDestinationId(dest.getId(), request.getTravelDate().getDayOfWeek().getValue())).thenReturn(List.of());
        when(transportationService.findFlightTransferListByOriginsDestinations(origin.getId(), request.getTravelDate().getDayOfWeek().getValue())).thenReturn(List.of());
        List<RouteResponse> routes = routeServiceImpl.findAllValidRoutes(request);
        assertThat(routes).hasSize(1);
        assertThat(routes.get(0).getSegments()).hasSize(1);
        assertThat(routes.get(0).getSegments().get(0).getTransportationType()).isEqualTo(TransportationType.FLIGHT);
        assertThat(routes.get(0).getSegments().get(0).getOrigin()).isEqualTo(origin);
        assertThat(routes.get(0).getSegments().get(0).getDestination()).isEqualTo(dest);
    }

    @Test
    void findAllValidRoutes_noRoutes() {
        RouteSearchRequest request = new RouteSearchRequest();
        request.setOriginLocationCode("IST");
        request.setDestinationLocationCode("ANK");
        request.setTravelDate(LocalDate.now());
        LocationResponse origin = new LocationResponse();
        origin.setId(1L);
        LocationResponse dest = new LocationResponse();
        dest.setId(2L);
        when(locationService.findByLocationCode("IST")).thenReturn(Optional.of(origin));
        when(locationService.findByLocationCode("ANK")).thenReturn(Optional.of(dest));
        when(transportationService.findByOriginId(origin.getId(), request.getTravelDate().getDayOfWeek().getValue())).thenReturn(List.of());
        when(transportationService.findByDestinationId(dest.getId(), request.getTravelDate().getDayOfWeek().getValue())).thenReturn(List.of());
        when(transportationService.findFlightTransferListByOriginsDestinations(origin.getId(), request.getTravelDate().getDayOfWeek().getValue())).thenReturn(List.of());
        List<RouteResponse> routes = routeServiceImpl.findAllValidRoutes(request);
        assertThat(routes).isEmpty();
    }
}