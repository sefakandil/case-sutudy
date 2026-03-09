package com.example.demo.transportation;

import com.example.demo.common.exception.ApiException;
import com.example.demo.location.LocationService;
import com.example.demo.location.dto.LocationResponse;
import com.example.demo.transportation.dto.TransportationRequest;
import com.example.demo.transportation.dto.TransportationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransportationServiceImplTest {
    @Mock
    private LocationService locationService;
    @Mock
    private TransportationRepository transportationRepository;
    @Mock
    private TransportationMapper transportationMapper;
    @InjectMocks
    private TransportationServiceImpl transportationServiceImpl;

    @Test
    void create_originNotFound() {
        TransportationRequest request = mock(TransportationRequest.class);
        when(request.getOrigin()).thenReturn(mock(LocationResponse.class));
        when(request.getOrigin().getLocationCode()).thenReturn("IST");
        when(locationService.findByLocationCode("IST")).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> transportationServiceImpl.create(request));
        assertThat(thrown).isInstanceOf(ApiException.class);
        assertThat(((ApiException) thrown).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(((ApiException) thrown).getMessageKey()).isEqualTo("error.location.origin.notfound");
    }

    @Test
    void create_destinationNotFound() {
        TransportationRequest request = mock(TransportationRequest.class);
        LocationResponse origin = mock(LocationResponse.class);
        when(request.getOrigin()).thenReturn(origin);
        when(origin.getLocationCode()).thenReturn("IST");
        when(locationService.findByLocationCode("IST")).thenReturn(Optional.of(origin));
        LocationResponse destination = mock(LocationResponse.class);
        when(request.getDestination()).thenReturn(destination);
        when(destination.getLocationCode()).thenReturn("ANK");
        when(locationService.findByLocationCode("ANK")).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> transportationServiceImpl.create(request));
        assertThat(thrown).isInstanceOf(ApiException.class);
        assertThat(((ApiException) thrown).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(((ApiException) thrown).getMessageKey()).isEqualTo("error.location.destination.notfound");
    }

    @Test
    void create_samePoints() {
        TransportationRequest request = mock(TransportationRequest.class);
        LocationResponse origin = mock(LocationResponse.class);
        LocationResponse destination = mock(LocationResponse.class);
        when(request.getOrigin()).thenReturn(origin);
        when(request.getDestination()).thenReturn(destination);
        when(origin.getLocationCode()).thenReturn("IST");
        when(destination.getLocationCode()).thenReturn("IST");
        when(locationService.findByLocationCode("IST")).thenReturn(Optional.of(origin));
        when(locationService.findByLocationCode("IST")).thenReturn(Optional.of(destination));
        when(request.getTransportationType()).thenReturn(TransportationType.BUS);
        Throwable thrown = catchThrowable(() -> transportationServiceImpl.create(request));
        assertThat(thrown).isInstanceOf(ApiException.class);
        assertThat(((ApiException) thrown).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((ApiException) thrown).getMessageKey()).isEqualTo("error.route.same.points");
    }

    @Test
    void create_flightCodeLength() {
        TransportationRequest request = mock(TransportationRequest.class);
        LocationResponse origin = mock(LocationResponse.class);
        LocationResponse destination = mock(LocationResponse.class);
        when(request.getOrigin()).thenReturn(origin);
        when(request.getDestination()).thenReturn(destination);
        when(origin.getLocationCode()).thenReturn("IS");
        when(destination.getLocationCode()).thenReturn("ANK");
        when(locationService.findByLocationCode("IS")).thenReturn(Optional.of(origin));
        when(locationService.findByLocationCode("ANK")).thenReturn(Optional.of(destination));
        when(request.getTransportationType()).thenReturn(TransportationType.FLIGHT);
        Throwable thrown = catchThrowable(() -> transportationServiceImpl.create(request));
        assertThat(thrown).isInstanceOf(ApiException.class);
        assertThat(((ApiException) thrown).getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
        assertThat(((ApiException) thrown).getMessageKey()).isEqualTo("error.flight.code.length");
    }

    @Test
    void create_success() {
        TransportationRequest request = mock(TransportationRequest.class);
        LocationResponse origin = mock(LocationResponse.class);
        LocationResponse destination = mock(LocationResponse.class);
        when(request.getOrigin()).thenReturn(origin);
        when(request.getDestination()).thenReturn(destination);
        when(origin.getLocationCode()).thenReturn("IST");
        when(destination.getLocationCode()).thenReturn("ANK");
        when(locationService.findByLocationCode("IST")).thenReturn(Optional.of(origin));
        when(locationService.findByLocationCode("ANK")).thenReturn(Optional.of(destination));
        when(request.getTransportationType()).thenReturn(TransportationType.BUS);
        Transportation transportation = mock(Transportation.class);
        when(transportationMapper.convertToTransportation(request, origin, destination)).thenReturn(transportation);
        when(transportationRepository.save(transportation)).thenReturn(transportation);
        TransportationResponse response = mock(TransportationResponse.class);
        when(transportationMapper.convertToTransportationResponse(transportation)).thenReturn(response);
        TransportationResponse result = transportationServiceImpl.create(request);
        assertThat(result).isEqualTo(response);
        verify(transportationMapper).convertToTransportation(request, origin, destination);
        verify(transportationRepository).save(transportation);
        verify(transportationMapper).convertToTransportationResponse(transportation);
    }

    @Test
    void findByDestinationId_success() {
        Long destinationId = 1L;
        int selectedDay = 2;
        List<Transportation> entities = List.of(mock(Transportation.class));
        List<TransportationResponse> responses = List.of(mock(TransportationResponse.class));
        when(transportationRepository.findByDestinationId(destinationId, 1 << (selectedDay - 1))).thenReturn(entities);
        when(transportationMapper.convertToTransportationResponseList(entities)).thenReturn(responses);
        List<TransportationResponse> result = transportationServiceImpl.findByDestinationId(destinationId, selectedDay);
        assertThat(result).isEqualTo(responses);
        verify(transportationRepository).findByDestinationId(destinationId, 1 << (selectedDay - 1));
        verify(transportationMapper).convertToTransportationResponseList(entities);
    }

    @Test
    void findByOriginId_success() {
        Long originId = 1L;
        int selectedDay = 2;
        List<Transportation> entities = List.of(mock(Transportation.class));
        List<TransportationResponse> responses = List.of(mock(TransportationResponse.class));
        when(transportationRepository.findByOriginId(originId, 1 << (selectedDay - 1))).thenReturn(entities);
        when(transportationMapper.convertToTransportationResponseList(entities)).thenReturn(responses);
        List<TransportationResponse> result = transportationServiceImpl.findByOriginId(originId, selectedDay);
        assertThat(result).isEqualTo(responses);
        verify(transportationRepository).findByOriginId(originId, 1 << (selectedDay - 1));
        verify(transportationMapper).convertToTransportationResponseList(entities);
    }

    @Test
    void findFlightTransferListByOriginsDestinations_success() {
        Long originId = 1L;
        int selectedDay = 2;
        List<Transportation> entities = List.of(mock(Transportation.class));
        List<TransportationResponse> responses = List.of(mock(TransportationResponse.class));
        when(transportationRepository.findTransferListByOriginIdAndTransferType(originId, 1 << (selectedDay - 1), TransportationType.FLIGHT.toString())).thenReturn(entities);
        when(transportationMapper.convertToTransportationResponseList(entities)).thenReturn(responses);
        List<TransportationResponse> result = transportationServiceImpl.findFlightTransferListByOriginsDestinations(originId, selectedDay);
        assertThat(result).isEqualTo(responses);
        verify(transportationRepository).findTransferListByOriginIdAndTransferType(originId, 1 << (selectedDay - 1), TransportationType.FLIGHT.toString());
        verify(transportationMapper).convertToTransportationResponseList(entities);
    }

    @Test
    void getAll_success() {
        PageRequest pageable = PageRequest.of(0, 10);
        Transportation entity = mock(Transportation.class);
        TransportationResponse response = mock(TransportationResponse.class);
        Page<Transportation> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(transportationRepository.findAll(pageable)).thenReturn(page);
        when(transportationMapper.convertToTransportationResponse(entity)).thenReturn(response);
        Page<TransportationResponse> result = transportationServiceImpl.getAll(pageable);
        assertThat(result.getContent()).containsExactly(response);
        verify(transportationRepository).findAll(pageable);
        verify(transportationMapper).convertToTransportationResponse(entity);
    }
}