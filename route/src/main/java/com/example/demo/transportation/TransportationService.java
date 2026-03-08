package com.example.demo.transportation;


import com.example.demo.transportation.dto.TransportationRequest;
import com.example.demo.transportation.dto.TransportationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransportationService {

    TransportationResponse create(TransportationRequest request);

    List<TransportationResponse> findByDestinationId(Long destinationId, int selectedDay);

    List<TransportationResponse> findByOriginId(Long originId, int selectedDay);

    List<TransportationResponse> findFlightTransferListByOriginsDestinations(Long originId, int selectedDay);

    Page<TransportationResponse> getAll(Pageable pageable);

}