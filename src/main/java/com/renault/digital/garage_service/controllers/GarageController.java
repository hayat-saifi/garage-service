package com.renault.digital.garage_service.controllers;

import com.renault.digital.garage_service.models.GarageModel;
import com.renault.digital.garage_service.services.GarageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/garages")
@RequiredArgsConstructor
public class GarageController {

    private final GarageService garageService;

    @PostMapping
    public ResponseEntity<GarageModel> create(@RequestBody GarageModel garageModel) {
        GarageModel created = garageService.create(garageModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GarageModel> update(
            @PathVariable Long id,
            @RequestBody GarageModel garageModel) {

        GarageModel updated = garageService.update(id, garageModel);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        garageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GarageModel> getById(@PathVariable Long id) {
        return ResponseEntity.ok(garageService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<GarageModel>> getAll(Pageable pageable) {
        return ResponseEntity.ok(garageService.getAll(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<GarageModel>> search(
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String accessoryType,
            Pageable pageable) {

        Page<GarageModel> result = garageService.search(vehicleType, accessoryType, pageable);
        return ResponseEntity.ok(result);
    }
}