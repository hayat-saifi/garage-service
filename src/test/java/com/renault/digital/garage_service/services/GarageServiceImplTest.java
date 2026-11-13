package com.renault.digital.garage_service.services;

import com.renault.digital.garage_service.dto.GarageRequest;
import com.renault.digital.garage_service.dto.GarageResponse;
import com.renault.digital.garage_service.dto.OpeningTimeDto;
import com.renault.digital.garage_service.dto.PageResponse;
import com.renault.digital.garage_service.entities.Garage;
import com.renault.digital.garage_service.enums.FuelType;
import com.renault.digital.garage_service.exceptions.BadRequestException;
import com.renault.digital.garage_service.exceptions.NotFoundException;
import com.renault.digital.garage_service.mapper.GarageMapper;
import com.renault.digital.garage_service.repositories.GarageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GarageServiceImplTest {

    @Mock
    private GarageRepository garageRepository;

    @Mock
    private GarageMapper garageMapper;

    @InjectMocks
    private GarageServiceImpl garageService;

    // creation test
    @Test
    void shouldCreateGarageWhenRequestIsValid() {
        GarageRequest request = validRequest();
        Garage garage = new Garage();
        GarageResponse response = response();
        when(garageMapper.toEntity(request)).thenReturn(garage);
        when(garageRepository.save(garage)).thenReturn(garage);
        when(garageMapper.toResponse(garage)).thenReturn(response);

        GarageResponse result = garageService.createGarage(request);

        assertThat(result).isEqualTo(response);
        verify(garageRepository).save(garage);
    }

    @Test
    void shouldRejectGarageWhenEndTimeIsBeforeStartTime() {
        GarageRequest request = requestWith(Map.of(DayOfWeek.MONDAY,
                List.of(new OpeningTimeDto(LocalTime.of(18, 0), LocalTime.of(9, 0)))));

        assertThatThrownBy(() -> garageService.createGarage(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("MONDAY");

        verifyNoInteractions(garageRepository);
    }

    @Test
    void shouldRejectGarageWhenEndTimeEqualsStartTime() {
        GarageRequest request = requestWith(Map.of(DayOfWeek.MONDAY,
                List.of(new OpeningTimeDto(LocalTime.of(9, 0), LocalTime.of(9, 0)))));

        assertThatThrownBy(() -> garageService.createGarage(request))
                .isInstanceOf(BadRequestException.class);

        verifyNoInteractions(garageRepository);
    }

    //rejeter le garage quand les creneaux se chevauchent le mm jour
    @Test
    void shouldRejectGarageWhenOpeningSlotsOverlapTheSameDay() {
        GarageRequest request = requestWith(Map.of(DayOfWeek.TUESDAY, List.of(
                new OpeningTimeDto(LocalTime.of(9, 0), LocalTime.of(13, 0)),
                new OpeningTimeDto(LocalTime.of(12, 0), LocalTime.of(18, 0)))));

        assertThatThrownBy(() -> garageService.createGarage(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("TUESDAY");

        verifyNoInteractions(garageRepository);
    }



    //  getGarageById

    @Test
    void shouldReturnGarageWhenIdExists() {
        Garage garage = new Garage();
        GarageResponse response = response();
        when(garageRepository.findById(1L)).thenReturn(Optional.of(garage));
        when(garageMapper.toResponse(garage)).thenReturn(response);

        assertThat(garageService.getGarageById(1L)).isEqualTo(response);
    }

    @Test
    void shouldThrowNotFoundWhenGarageDoesNotExist() {
        when(garageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> garageService.getGarageById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    // getAllGarages

    @Test
    void shouldReturnPagedGarages() {
        Pageable pageable = PageRequest.of(0, 10);
        Garage garage = new Garage();
        GarageResponse response = response();
        when(garageRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(garage), pageable, 1));
        when(garageMapper.toResponse(garage)).thenReturn(response);

        PageResponse<GarageResponse> result = garageService.getAllGarages(pageable);

        assertThat(result.content()).containsExactly(response);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
    }

    //  updateGarage

    @Test
    void shouldUpdateGarageWhenIdExists() {
        GarageRequest request = validRequest();
        Garage garage = new Garage();
        GarageResponse response = response();
        when(garageRepository.findById(1L)).thenReturn(Optional.of(garage));
        when(garageMapper.toResponse(garage)).thenReturn(response);

        GarageResponse result = garageService.updateGarage(1L, request);

        assertThat(result).isEqualTo(response);
        verify(garageMapper).update(garage, request);
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingUnknownGarage() {
        GarageRequest request = validRequest();
        when(garageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> garageService.updateGarage(99L, request))
                .isInstanceOf(NotFoundException.class);

        verify(garageMapper, never()).update(any(), any());
    }

    // deleteGarage

    @Test
    void shouldDeleteGarageWhenIdExists() {
        Garage garage = new Garage();
        when(garageRepository.findById(1L)).thenReturn(Optional.of(garage));

        garageService.deleteGarage(1L);

        verify(garageRepository).delete(garage);
    }

    @Test
    void shouldThrowNotFoundWhenDeletingUnknownGarage() {
        when(garageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> garageService.deleteGarage(99L))
                .isInstanceOf(NotFoundException.class);

        verify(garageRepository, never()).delete(any(Garage.class));
    }

    // searchGarages

    @Test
    void shouldSearchGaragesByFuelTypeAndAccessory() {
        Pageable pageable = PageRequest.of(0, 10);
        Garage garage = new Garage();
        GarageResponse response = response();
        when(garageRepository.findAll(ArgumentMatchers.<Specification<Garage>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(garage), pageable, 1));
        when(garageMapper.toResponse(garage)).thenReturn(response);

        PageResponse<GarageResponse> result = garageService.searchGarages(FuelType.ELECTRIC, "GPS", pageable);

        assertThat(result.content()).containsExactly(response);
        assertThat(result.totalElements()).isEqualTo(1);
    }




    private GarageRequest validRequest() {
        return requestWith(Map.of(DayOfWeek.MONDAY, List.of(
                new OpeningTimeDto(LocalTime.of(9, 0), LocalTime.of(12, 30)),
                new OpeningTimeDto(LocalTime.of(14, 0), LocalTime.of(18, 0)))));
    }

    private GarageRequest requestWith(Map<DayOfWeek, List<OpeningTimeDto>> horaires) {
        return new GarageRequest("Renault Casablanca", "12 Bd Zerktouni", "+212 522 00 00 00",
                "casa@renault-garages.ma", horaires);
    }

    private GarageResponse response() {
        return new GarageResponse(1L, "Renault Casablanca", "12 Bd Zerktouni", "+212 522 00 00 00",
                "casa@renault-garages.ma",
                Map.of(DayOfWeek.MONDAY, List.of(new OpeningTimeDto(LocalTime.of(9, 0), LocalTime.of(18, 0)))));
    }
}