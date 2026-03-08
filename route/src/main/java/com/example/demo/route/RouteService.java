package com.example.demo.route;

import com.example.demo.route.dto.RouteResponse;
import com.example.demo.route.dto.RouteSearchRequest;

import java.util.List;

public interface RouteService {

    List<RouteResponse> findAllValidRoutes(RouteSearchRequest request);

}
