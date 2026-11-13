package com.renault.digital.garage_service.mapper;

import com.renault.digital.garage_service.dto.GarageRequest;
import com.renault.digital.garage_service.dto.GarageResponse;
import com.renault.digital.garage_service.dto.OpeningTimeDto;
import com.renault.digital.garage_service.entities.Garage;
import com.renault.digital.garage_service.entities.OpeningTime;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class GarageMapper {

    public Garage toEntity(GarageRequest request) {
        Garage garage = new Garage();
        update(garage, request);
        return garage;
    }

    /** Copie tous les champs de la requête dans le garage (utilisé pour la création et le PUT). */
    public void update(Garage garage, GarageRequest request) {
        garage.setName(request.name().trim());
        garage.setAddress(request.address().trim());
        garage.setTelephone(request.telephone().trim());
        garage.setEmail(request.email().trim());

        garage.getOpeningTimes().clear();
        request.horairesOuverture().forEach((day, slots) ->
                slots.forEach(slot ->
                        garage.getOpeningTimes().add(new OpeningTime(day, slot.startTime(), slot.endTime()))));
    }

    public GarageResponse toResponse(Garage garage) {
        Map<DayOfWeek, List<OpeningTimeDto>> horaires = new EnumMap<>(DayOfWeek.class);
        for (OpeningTime openingTime : garage.getOpeningTimes()) {
            horaires.computeIfAbsent(openingTime.getDayOfWeek(), day -> new ArrayList<>())
                    .add(new OpeningTimeDto(openingTime.getStartTime(), openingTime.getEndTime()));
        }
        horaires.values().forEach(slots -> slots.sort(Comparator.comparing(OpeningTimeDto::startTime)));

        return new GarageResponse(
                garage.getId(),
                garage.getName(),
                garage.getAddress(),
                garage.getTelephone(),
                garage.getEmail(),
                horaires);
    }
}