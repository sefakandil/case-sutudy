package com.example.demo.route;

import com.example.demo.common.exception.ApiException;
import com.example.demo.location.LocationService;
import com.example.demo.location.dto.LocationResponse;
import com.example.demo.route.dto.RouteResponse;
import com.example.demo.route.dto.RouteSearchRequest;
import com.example.demo.route.dto.RouteSegmentResponse;
import com.example.demo.transportation.TransportationService;
import com.example.demo.transportation.TransportationType;
import com.example.demo.transportation.dto.TransportationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final LocationService locationService;

    private final TransportationService transportationService;

    @Override
    @Cacheable(
            value = "routes:search",
            key = "#request.originLocationCode + '-' + #request.destinationLocationCode + '-' + #request.travelDate"
    )
    public List<RouteResponse> findAllValidRoutes(RouteSearchRequest request) {

        final int selectedDay = request.getTravelDate().getDayOfWeek().getValue();

        final LocationResponse origin = locationService.findByLocationCode(request.getOriginLocationCode())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "error.location.origin.notfound",
                        request.getOriginLocationCode()
                ));

        final LocationResponse destination = locationService.findByLocationCode(request.getDestinationLocationCode())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "error.location.destination.notfound",
                        request.getDestinationLocationCode()
                ));

        final List<TransportationResponse> originList = transportationService.findByOriginId(origin.getId(), selectedDay);
        final List<TransportationResponse> destinationList = transportationService.findByDestinationId(destination.getId(), selectedDay);
        final List<TransportationResponse> transferListByOriginsDestinations = transportationService.findFlightTransferListByOriginsDestinations(origin.getId(), selectedDay);

        final HashMap<String, List<TransportationResponse>> originsDestinationsMap = originList.stream().collect(Collectors.groupingBy(t -> t.getDestination().getLocationCode(), HashMap::new, Collectors.toList()));
        final HashMap<String, List<TransportationResponse>> destinationsOriginMap = destinationList.stream().collect(Collectors.groupingBy(t -> t.getOrigin().getLocationCode(), HashMap::new, Collectors.toList()));
        final HashMap<String, TransportationResponse> transferMap = transferListByOriginsDestinations.stream().collect(Collectors.toMap(
                t -> t.getDestination().getLocationCode(),
                trn -> trn,
                (oldValue, newValue) -> newValue,
                HashMap::new
        ));
        final List<RouteResponse> routes = new ArrayList<>();

        findDirectFlights(originsDestinationsMap, destination, routes);
        findAfterFlight(originsDestinationsMap, destinationsOriginMap, routes);
        findBeforeFlight(originsDestinationsMap, destinationsOriginMap, routes);
        findCompleteTransfers(originsDestinationsMap, destinationsOriginMap, transferMap, routes);

        return routes;
    }

    private void findDirectFlights(HashMap<String, List<TransportationResponse>> originsDestinationsMap, LocationResponse destination, List<RouteResponse> routes) {

        final List<TransportationResponse> directRoutes = originsDestinationsMap.get(destination.getLocationCode());

        if (CollectionUtils.isEmpty(directRoutes)) {
            return;
        }

        final TransportationResponse route = directRoutes.stream()
                .filter(data -> TransportationType.FLIGHT.equals(data.getTransportationType()))
                .findFirst().orElse(null);

        if (Objects.isNull(route)) {
            return;

        }

        final RouteResponse routeResponse = new RouteResponse();
        final RouteSegmentResponse routeSegmentResponse = new RouteSegmentResponse();
        routeSegmentResponse.setOrigin(route.getOrigin());
        routeSegmentResponse.setDestination(route.getDestination());
        routeSegmentResponse.setTransportationType(route.getTransportationType());
        routeResponse.setSegments(List.of(routeSegmentResponse));
        routes.add(routeResponse);

    }

    private void findAfterFlight(HashMap<String, List<TransportationResponse>> originsDestinationsMap, HashMap<String, List<TransportationResponse>> destinationsOriginMap, List<RouteResponse> routes) {

        final Set<String> commonKeys = new HashSet<>(originsDestinationsMap.keySet());
        commonKeys.retainAll(destinationsOriginMap.keySet());

        if (CollectionUtils.isEmpty(commonKeys)) {
            return;
        }

        final List<TransportationResponse> firstFlights = new ArrayList<>();
        commonKeys.stream().forEach(locationCode -> {

            firstFlights.addAll(originsDestinationsMap.get(locationCode).stream()
                    .filter(data -> TransportationType.FLIGHT.equals(data.getTransportationType()))
                    .toList());
        });

        if (CollectionUtils.isEmpty(firstFlights)) {
            return;
        }

        firstFlights.forEach(originRoute -> {

            destinationsOriginMap.get(originRoute.getDestination().getLocationCode()).stream()
                    .filter(data -> !TransportationType.FLIGHT.equals(data.getTransportationType()))
                    .forEach(secondRoute -> {
                        final RouteResponse routeResponse = new RouteResponse();
                        final RouteSegmentResponse firstSegmentResponse = new RouteSegmentResponse();
                        firstSegmentResponse.setOrigin(originRoute.getOrigin());
                        firstSegmentResponse.setDestination(originRoute.getDestination());
                        firstSegmentResponse.setTransportationType(originRoute.getTransportationType());
                        final RouteSegmentResponse secondSegmentResponse = new RouteSegmentResponse();
                        secondSegmentResponse.setOrigin(secondRoute.getOrigin());
                        secondSegmentResponse.setDestination(secondRoute.getDestination());
                        secondSegmentResponse.setTransportationType(secondRoute.getTransportationType());
                        routeResponse.setSegments(List.of(firstSegmentResponse, secondSegmentResponse));
                        routes.add(routeResponse);
                    });
        });

    }

    private void findBeforeFlight(HashMap<String, List<TransportationResponse>> originsDestinationsMap, HashMap<String, List<TransportationResponse>> destinationsOriginMap, List<RouteResponse> routes) {

        final Set<String> commonKeys = new HashSet<>(originsDestinationsMap.keySet());
        commonKeys.retainAll(destinationsOriginMap.keySet());

        if (CollectionUtils.isEmpty(commonKeys)) {
            return;
        }

        final List<TransportationResponse> firstRoute = new ArrayList<>();
        commonKeys.stream().forEach(locationCode -> {

            firstRoute.addAll(originsDestinationsMap.get(locationCode).stream()
                    .filter(data -> !TransportationType.FLIGHT.equals(data.getTransportationType()))
                    .toList());
        });

        if (CollectionUtils.isEmpty(firstRoute)) {
            return;
        }

        firstRoute.forEach(originRoute -> {

            destinationsOriginMap.get(originRoute.getDestination().getLocationCode()).stream()
                    .filter(data -> TransportationType.FLIGHT.equals(data.getTransportationType()))
                    .forEach(secondRoute -> {

                        final RouteResponse routeResponse = new RouteResponse();
                        final RouteSegmentResponse firstSegmentResponse = new RouteSegmentResponse();
                        firstSegmentResponse.setOrigin(originRoute.getOrigin());
                        firstSegmentResponse.setDestination(originRoute.getDestination());
                        firstSegmentResponse.setTransportationType(originRoute.getTransportationType());
                        final RouteSegmentResponse secondSegmentResponse = new RouteSegmentResponse();
                        secondSegmentResponse.setOrigin(secondRoute.getOrigin());
                        secondSegmentResponse.setDestination(secondRoute.getDestination());
                        secondSegmentResponse.setTransportationType(secondRoute.getTransportationType());
                        routeResponse.setSegments(List.of(firstSegmentResponse, secondSegmentResponse));
                        routes.add(routeResponse);
                    });
        });
    }


    private void findCompleteTransfers(HashMap<String, List<TransportationResponse>> originsDestinationsMap, HashMap<String, List<TransportationResponse>> destinationsOriginMap, HashMap<String, TransportationResponse> transferMap, List<RouteResponse> routes) {

        final Set<String> commonKeys = new HashSet<>(destinationsOriginMap.keySet());
        commonKeys.retainAll(transferMap.keySet());

        if (CollectionUtils.isEmpty(commonKeys)) {
            return;
        }

        final List<TransportationResponse> lastRoute = new ArrayList<>();

        commonKeys.stream().forEach(locationCode -> {
            lastRoute.addAll(destinationsOriginMap.get(locationCode).stream()
                    .filter(data -> !TransportationType.FLIGHT.equals(data.getTransportationType()))
                    .toList());

        });

        if (CollectionUtils.isEmpty(lastRoute)) {
            return;
        }

        lastRoute.forEach(last -> {

            final TransportationResponse secondRoute = transferMap.get(last.getOrigin().getLocationCode());

            final  List<TransportationResponse> firstRouteList = originsDestinationsMap.get(secondRoute.getOrigin().getLocationCode());

            if(!CollectionUtils.isEmpty(firstRouteList)){

            firstRouteList.stream()
                    .filter(data -> !TransportationType.FLIGHT.equals(data.getTransportationType()))
                    .forEach(first -> {

                        final RouteResponse routeResponse = new RouteResponse();
                        final RouteSegmentResponse firstSegmentResponse = new RouteSegmentResponse();
                        firstSegmentResponse.setOrigin(first.getOrigin());
                        firstSegmentResponse.setDestination(first.getDestination());
                        firstSegmentResponse.setTransportationType(first.getTransportationType());
                        final RouteSegmentResponse secondSegmentResponse = new RouteSegmentResponse();
                        secondSegmentResponse.setOrigin(secondRoute.getOrigin());
                        secondSegmentResponse.setDestination(secondRoute.getDestination());
                        secondSegmentResponse.setTransportationType(secondRoute.getTransportationType());
                        final RouteSegmentResponse thirdSegmentResponse = new RouteSegmentResponse();
                        thirdSegmentResponse.setOrigin(last.getOrigin());
                        thirdSegmentResponse.setDestination(last.getDestination());
                        thirdSegmentResponse.setTransportationType(last.getTransportationType());
                        routeResponse.setSegments(List.of(firstSegmentResponse, secondSegmentResponse, thirdSegmentResponse));
                        routes.add(routeResponse);
                    });
            }
        });


    }
}
