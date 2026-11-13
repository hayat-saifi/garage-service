package com.renault.digital.garage_service.dto;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public record GarageResponse(
        Long id,
        String name,
        String address,
        String telephone,
        String email,
        Map<DayOfWeek, List<OpeningTimeDto>> horairesOuverture) {
}