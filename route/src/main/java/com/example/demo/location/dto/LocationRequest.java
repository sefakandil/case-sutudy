package com.example.demo.location.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LocationRequest {

    @NotBlank(message = "{validation.location.name.required}")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "{validation.location.name.onlyLetters}")
    private String name;

    @NotBlank(message = "{validation.location.country.required}")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "{validation.location.country.onlyLetters}")
    private String country;

    @NotBlank(message = "{validation.location.city.required}")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "{validation.location.city.onlyLetters}")
    private String city;

    @NotBlank(message = "{validation.location.code.required}")
    @Size(min = 3, max = 10, message = "{validation.location.code.size}")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "{validation.location.code.alphanumeric}")
    private String locationCode;
}
