package com.example.demo.transportation;

import com.example.demo.common.exception.ApiException;
import com.example.demo.location.LocationService;
import com.example.demo.location.dto.LocationResponse;
import com.example.demo.transportation.dto.TransportationRequest;
import com.example.demo.transportation.dto.TransportationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransportationServiceImpl implements TransportationService {

    private static final int FLIGHT_LENGTH = 3;

    private final LocationService locationService;

    private final TransportationRepository transportationRepository;

    private final TransportationMapper transportationMapper;


    @Override
    @Caching(evict = {
            @CacheEvict(value = "transportation:page", allEntries = true),
            @CacheEvict(value = "transportation:by-destination-day", allEntries = true),
            @CacheEvict(value = "transportation:by-origin-day", allEntries = true),
            @CacheEvict(value = "transportation:flight-transfer-by-origin-day", allEntries = true),
            @CacheEvict(value = "routes:search", allEntries = true)
    })
    public TransportationResponse create(TransportationRequest request) {

         final LocationResponse origin = locationService.findByLocationCode(request.getOrigin().getLocationCode())
                .orElseThrow(() ->new ApiException(
                        HttpStatus.CONFLICT,
                        "error.location.origin.notfound",
                        request.getOrigin().getLocationCode()
                ));

        final LocationResponse destination = locationService.findByLocationCode(request.getDestination().getLocationCode())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.CONFLICT,
                        "error.location.destination.notfound",
                        request.getDestination().getLocationCode()
                ));

        checkTransportationType(request.getTransportationType(), origin, destination);

        final Transportation transportation = transportationMapper.convertToTransportation(request, origin, destination);

        Transportation entity = transportationRepository.save(transportation);

        return transportationMapper.convertToTransportationResponse(entity);
    }

    private void checkTransportationType(TransportationType transportationType, LocationResponse origin, LocationResponse destination) {
        final String originCode = origin.getLocationCode();
        final String destinationCode = destination.getLocationCode();

        if (originCode.equals(destinationCode)) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "error.route.same.points"
            );
        }

        if (!TransportationType.FLIGHT.equals(transportationType)) {
            return;
        }

        if (originCode == null || destinationCode == null || FLIGHT_LENGTH != originCode.length()  || FLIGHT_LENGTH != destinationCode.length()) {

            throw new ApiException(
                    HttpStatus.UNPROCESSABLE_CONTENT,
                    "error.flight.code.length",
                    originCode, destinationCode, FLIGHT_LENGTH
            );
        }

    }

    @Override
    @Cacheable(value = "transportation:by-destination-day", key = "#destinationId + '-' + #selectedDay")
    public List<TransportationResponse> findByDestinationId(Long destinationId, int selectedDay) {
        int dayBit = 1 << (selectedDay - 1);
        final List<Transportation> transportations = transportationRepository.findByDestinationId(destinationId, dayBit);
        return transportationMapper.convertToTransportationResponseList(transportations);

    }

    @Override
    @Cacheable(value = "transportation:by-origin-day", key = "#originId + '-' + #selectedDay")
    public List<TransportationResponse> findByOriginId(Long originId, int selectedDay) {
        int dayBit = 1 << (selectedDay - 1);
        final List<Transportation> transportations = transportationRepository.findByOriginId(originId, dayBit);

        return transportationMapper.convertToTransportationResponseList(transportations);
    }

    @Override
   @Cacheable(value = "transportation:by-transfer-origin-day", key = "#originId + '-' + #selectedDay")
    public List<TransportationResponse> findFlightTransferListByOriginsDestinations(Long originId, int selectedDay) {
        int dayBit = 1 << (selectedDay - 1);
        final List<Transportation> transportations = transportationRepository.findTransferListByOriginIdAndTransferType(originId, dayBit, TransportationType.FLIGHT.toString());
       return transportationMapper.convertToTransportationResponseList(transportations);
    }

    @Override
    @Cacheable(
            value = "transportation:page",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize"
    )
    public Page<TransportationResponse> getAll(Pageable pageable) {
        return transportationRepository.findAll(pageable)
                .map(transportationMapper::convertToTransportationResponse);
    }

}
