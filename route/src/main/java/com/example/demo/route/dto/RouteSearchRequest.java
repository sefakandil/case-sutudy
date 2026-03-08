package com.example.demo.route.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RouteSearchRequest {

    @NotBlank(message = "{validation.origin.code.required}")
    @Size(min = 3, max = 10, message = "{validation.origin.code.size}")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "{validation.origin.code.pattern}")
    private String originLocationCode;

    @NotBlank(message = "{validation.destination.code.required}")
    @Size(min = 3, max = 10, message = "{validation.destination.code.size}")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "{validation.destination.code.pattern}")
    private String destinationLocationCode;

    @NotNull
    private LocalDate travelDate;
}

