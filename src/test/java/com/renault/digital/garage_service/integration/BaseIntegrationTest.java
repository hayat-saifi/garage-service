package com.renault.digital.garage_service.integration;

import com.jayway.jsonpath.JsonPath;
import com.renault.digital.garage_service.repositories.AccessoryRepository;
import com.renault.digital.garage_service.repositories.GarageRepository;
import com.renault.digital.garage_service.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
abstract class BaseIntegrationTest {

    protected static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected GarageRepository garageRepository;

    @Autowired
    protected VehicleRepository vehicleRepository;

    @Autowired
    protected AccessoryRepository accessoryRepository;

    @BeforeEach
    void cleanDatabase() {
        // Ordre imposé par les clés étrangère
        accessoryRepository.deleteAllInBatch();
        vehicleRepository.deleteAllInBatch();
        garageRepository.deleteAll();
    }

    // creation JSON

    protected String garageJson(String name) {
        return """
                {
                  "name": "%s",
                  "address": "12 Boulevard Zerktouni, Casablanca",
                  "telephone": "+212 522 00 00 00",
                  "email": "contact@renault-garage.ma",
                  "horaires_ouverture": {
                    "MONDAY": [
                      {"start_time": "09:00", "end_time": "12:30"},
                      {"start_time": "14:00", "end_time": "18:00"}
                    ],
                    "TUESDAY": [{"start_time": "09:00", "end_time": "18:00"}]
                  }
                }""".formatted(name);
    }

    protected String vehicleJson(String model, String fuel) {
        return """
                {"brand": "Renault", "model": "%s", "annee_fabrication": 2022, "type_carburant": "%s"}"""
                .formatted(model, fuel);
    }

    protected String accessoryJson(String nom) {
        return """
                {"nom": "%s", "description": "Accessoire de test", "prix": 350.00, "type": "ELECTRONIQUE"}"""
                .formatted(nom);
    }

    // création via l'API

    protected long createGarage(String name) throws Exception {
        return idOf(mockMvc.perform(post("/api/garages").contentType(JSON).content(garageJson(name)))
                .andExpect(status().isCreated()));
    }

    protected long createVehicle(long garageId, String model, String fuel) throws Exception {
        return idOf(mockMvc.perform(post("/api/garages/{id}/vehicles", garageId)
                        .contentType(JSON).content(vehicleJson(model, fuel)))
                .andExpect(status().isCreated()));
    }

    protected long createAccessory(long vehicleId, String nom) throws Exception {
        return idOf(mockMvc.perform(post("/api/vehicles/{id}/accessories", vehicleId)
                        .contentType(JSON).content(accessoryJson(nom)))
                .andExpect(status().isCreated()));
    }

    private long idOf(ResultActions actions) throws Exception {
        String body = actions.andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }
}