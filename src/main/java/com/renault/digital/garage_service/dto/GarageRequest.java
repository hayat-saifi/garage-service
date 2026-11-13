package com.renault.digital.garage_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public record GarageRequest(
        @NotBlank String name,
        @NotBlank String address,
        @NotBlank @Pattern(regexp = "^[+0-9 ().-]{6,20}$", message = "numéro de téléphone invalide") String telephone,
        @NotBlank @Email String email,
        @NotEmpty Map<DayOfWeek, @NotEmpty List<@Valid OpeningTimeDto>> horairesOuverture) {
}