package com.example.demo.transportation;

import com.example.demo.transportation.dto.TransportationRequest;
import com.example.demo.transportation.dto.TransportationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transportations")
@RequiredArgsConstructor
public class TransportationController {

    private final TransportationService transportationService;

    @PostMapping
    public TransportationResponse create(@RequestBody TransportationRequest request) {
        return transportationService.create(request);
    }

    @GetMapping
    public Page<TransportationResponse> getAll(
            @PageableDefault(sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return transportationService.getAll(pageable);
    }
}
