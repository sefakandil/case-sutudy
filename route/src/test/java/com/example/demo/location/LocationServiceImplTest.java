package com.example.demo.location;

import com.example.demo.common.exception.ApiException;
import com.example.demo.location.dto.LocationRequest;
import com.example.demo.location.dto.LocationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private LocationMapper locationMapper;
    @InjectMocks
    private LocationServiceImpl locationServiceImpl;

    @Test
    void create_success() {
        LocationRequest request = new LocationRequest();
        Location entity = new Location();
        LocationResponse response = new LocationResponse();
        when(locationMapper.convertToLocation(request)).thenReturn(entity);
        when(locationRepository.save(entity)).thenReturn(entity);
        when(locationMapper.convertToLocationResponse(entity)).thenReturn(response);
        LocationResponse result = locationServiceImpl.create(request);
        assertThat(result).isEqualTo(response);
        verify(locationMapper).convertToLocation(request);
        verify(locationRepository).save(entity);
        verify(locationMapper).convertToLocationResponse(entity);
    }

    @Test
    void getById_found() {
        Long id = 1L;
        Location entity = new Location();
        LocationResponse response = new LocationResponse();
        when(locationRepository.findById(id)).thenReturn(Optional.of(entity));
        when(locationMapper.convertToLocationResponse(entity)).thenReturn(response);
        LocationResponse result = locationServiceImpl.getById(id);
        assertThat(result).isEqualTo(response);
        verify(locationRepository).findById(id);
        verify(locationMapper).convertToLocationResponse(entity);
    }

    @Test
    void getById_notFound() {
        Long id = 2L;
        when(locationRepository.findById(id)).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> locationServiceImpl.getById(id));
        assertThat(thrown).isInstanceOf(ApiException.class)
            .hasMessageContaining("error.location.notfound");
        verify(locationRepository).findById(id);
    }

    @Test
    void getAll_success() {
        Location entity1 = new Location();
        Location entity2 = new Location();
        LocationResponse response1 = new LocationResponse();
        LocationResponse response2 = new LocationResponse();
        when(locationRepository.findAll()).thenReturn(List.of(entity1, entity2));
        when(locationMapper.convertToLocationResponse(entity1)).thenReturn(response1);
        when(locationMapper.convertToLocationResponse(entity2)).thenReturn(response2);
        List<LocationResponse> result = locationServiceImpl.getAll();
        assertThat(result).containsExactly(response1, response2);
        verify(locationRepository).findAll();
        verify(locationMapper).convertToLocationResponse(entity1);
        verify(locationMapper).convertToLocationResponse(entity2);
    }

    @Test
    void findByLocationCode_found() {
        String code = "LOC123";
        Location entity = new Location();
        LocationResponse response = new LocationResponse();
        when(locationRepository.findByLocationCode(code)).thenReturn(Optional.of(entity));
        when(locationMapper.convertToLocationResponse(entity)).thenReturn(response);
        Optional<LocationResponse> result = locationServiceImpl.findByLocationCode(code);
        assertThat(result).isPresent().contains(response);
        verify(locationRepository).findByLocationCode(code);
        verify(locationMapper).convertToLocationResponse(entity);
    }

    @Test
    void findByLocationCode_notFound() {
        String code = "LOC999";
        when(locationRepository.findByLocationCode(code)).thenReturn(Optional.empty());
        Optional<LocationResponse> result = locationServiceImpl.findByLocationCode(code);
        assertThat(result).isEmpty();
        verify(locationRepository).findByLocationCode(code);
    }

}