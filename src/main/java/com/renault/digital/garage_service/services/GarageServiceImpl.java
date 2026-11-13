package com.renault.digital.garage_service.services;

import com.renault.digital.garage_service.dto.GarageRequest;
import com.renault.digital.garage_service.dto.GarageResponse;
import com.renault.digital.garage_service.dto.OpeningTimeDto;
import com.renault.digital.garage_service.dto.PageResponse;
import com.renault.digital.garage_service.enums.FuelType;
import com.renault.digital.garage_service.entities.Garage;
import com.renault.digital.garage_service.exceptions.BadRequestException;
import com.renault.digital.garage_service.exceptions.NotFoundException;
import com.renault.digital.garage_service.mapper.GarageMapper;
import com.renault.digital.garage_service.repositories.GarageRepository;
import com.renault.digital.garage_service.specifications.GarageSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class GarageServiceImpl implements GarageService {

    private final GarageRepository garageRepository;
    private final GarageMapper garageMapper;

    @Override
    public GarageResponse createGarage(GarageRequest request) {
        validateOpeningHours(request.horairesOuverture());
        Garage garage = garageRepository.save(garageMapper.toEntity(request));
        return garageMapper.toResponse(garage);
    }

    @Override
    @Transactional(readOnly = true)
    public GarageResponse getGarageById(Long id) {
        return garageMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<GarageResponse> getAllGarages(Pageable pageable) {
        return PageResponse.from(garageRepository.findAll(pageable).map(garageMapper::toResponse));
    }

    @Override
    public GarageResponse updateGarage(Long id, GarageRequest request) {
        validateOpeningHours(request.horairesOuverture());
        Garage garage = findOrThrow(id);
        garageMapper.update(garage, request);
        return garageMapper.toResponse(garage);
    }

    @Override
    public void deleteGarage(Long id) {
        // Les véhicules (et leurs accessoires) sont supprimés en cascade.
        garageRepository.delete(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<GarageResponse> searchGarages(FuelType fuelType, String accessory, Pageable pageable) {
        Specification<Garage> spec = Specification.allOf(
                GarageSpecification.hasFuelType(fuelType),
                GarageSpecification.hasAccessory(accessory));
        return PageResponse.from(garageRepository.findAll(spec, pageable).map(garageMapper::toResponse));
    }

    private Garage findOrThrow(Long id) {
        return garageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Garage " + id + " introuvable"));
    }

    /** Un créneau doit finir après son début, et les créneaux d'un même jour ne doivent pas se chevaucher. */
    private void validateOpeningHours(Map<DayOfWeek, List<OpeningTimeDto>> horaires) {
        horaires.forEach((day, slots) -> {
            List<OpeningTimeDto> sorted = slots.stream()
                    .sorted(Comparator.comparing(OpeningTimeDto::startTime))
                    .toList();
            for (int i = 0; i < sorted.size(); i++) {
                OpeningTimeDto slot = sorted.get(i);
                if (!slot.endTime().isAfter(slot.startTime())) {
                    throw new BadRequestException(
                            "Créneau invalide le " + day + " : l'heure de fin doit être après l'heure de début");
                }
                if (i > 0 && slot.startTime().isBefore(sorted.get(i - 1).endTime())) {
                    throw new BadRequestException("Créneaux qui se chevauchent le " + day);
                }
            }
        });
    }
}