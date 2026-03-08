package com.example.demo.location;

import com.example.demo.common.exception.ApiException;
import com.example.demo.location.dto.LocationRequest;
import com.example.demo.location.dto.LocationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;


    @Override
    @Caching(evict = {
            @CacheEvict(value = "location:all", allEntries = true),
            @CacheEvict(value = "location:by-id", allEntries = true),
            @CacheEvict(value = "location:by-code", allEntries = true),
            @CacheEvict(value = "routes:search", allEntries = true)
    })
    public LocationResponse create(LocationRequest request) {

        final Location location = locationRepository.save(locationMapper.convertToLocation(request));

        if (Objects.nonNull(location)) {
            return locationMapper.convertToLocationResponse(location);
        }

        throw new ApiException(
                HttpStatus.CONFLICT,
                "error.location.create",
                request.getLocationCode()
        );

    }

    @Override
    @Cacheable("location:all")
    public List<LocationResponse> getAll() {
        return locationRepository.findAll()
                .stream()
                .map(data -> locationMapper.convertToLocationResponse(data))
                .toList();
    }

    @Override
    @Cacheable(value = "location:by-id", key = "#id")
    public LocationResponse getById(Long id) {

        final Optional<Location> location = locationRepository.findById(id);

        if (location.isPresent()) {
            return locationMapper.convertToLocationResponse(location.get());
        }

        throw new ApiException(
                HttpStatus.CONFLICT,
                "error.location.notfound",
                id
        );
    }

    @Override
   @Cacheable(value = "location:by-code", key = "#locationCode")
    public Optional<LocationResponse> findByLocationCode(String locationCode) {

        Optional<Location> location = locationRepository.findByLocationCode(locationCode);

        return Optional.ofNullable(location.map(data -> locationMapper.convertToLocationResponse(data)).orElse(null));
    }
}
