package com.renault.digital.garage_service.integration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GarageControllerIntegrationTest extends BaseIntegrationTest {

    //création
    @Test
    void shouldCreateGarageAndReturn201() throws Exception {
        mockMvc.perform(post("/api/garages").contentType(JSON).content(garageJson("Renault Casablanca")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Renault Casablanca"))
                .andExpect(jsonPath("$.horaires_ouverture.MONDAY.length()").value(2))
                .andExpect(jsonPath("$.horaires_ouverture.MONDAY[0].start_time").value(startsWith("09:00")))
                .andExpect(jsonPath("$.horaires_ouverture.MONDAY[1].start_time").value(startsWith("14:00")))
                .andExpect(jsonPath("$.horaires_ouverture.TUESDAY.length()").value(1));

        assertThat(garageRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        String body = garageJson("Garage").replace("contact@renault-garage.ma", "not-an-email");

        mockMvc.perform(post("/api/garages").contentType(JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details[0]").value(startsWith("email")));

        assertThat(garageRepository.count()).isZero();
    }

    @Test
    void shouldReturn400WhenOpeningSlotsOverlap() throws Exception {
        // le 2e créneau du lundi (12:00) commence avant la fin du 1er (12:30)
        String body = garageJson("Garage").replace("\"start_time\": \"14:00\"", "\"start_time\": \"12:00\"");

        mockMvc.perform(post("/api/garages").contentType(JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("MONDAY")));
    }

    @Test
    void shouldReturn400WhenEndTimeIsBeforeStartTime() throws Exception {
        String body = garageJson("Garage")
                .replace("\"start_time\": \"09:00\", \"end_time\": \"18:00\"",
                        "\"start_time\": \"18:00\", \"end_time\": \"09:00\"");

        mockMvc.perform(post("/api/garages").contentType(JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("TUESDAY")));
    }

    //lecture
    @Test
    void shouldReturnGarageById() throws Exception {
        long id = createGarage("Renault Rabat");

        mockMvc.perform(get("/api/garages/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) id))
                .andExpect(jsonPath("$.name").value("Renault Rabat"))
                .andExpect(jsonPath("$.email").value("contact@renault-garage.ma"));
    }

    @Test
    void shouldReturn404WhenGarageDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/garages/{id}", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("introuvable")));
    }

    @Test
    void shouldListGaragesWithPaginationAndSort() throws Exception {
        createGarage("B Garage");
        createGarage("A Garage");
        createGarage("C Garage");

        mockMvc.perform(get("/api/garages?page=0&size=2&sort=name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("A Garage"))
                .andExpect(jsonPath("$.content[1].name").value("B Garage"))
                .andExpect(jsonPath("$.total_elements").value(3))
                .andExpect(jsonPath("$.total_pages").value(2));
    }

    //modification
    @Test
    void shouldUpdateGarage() throws Exception {
        long id = createGarage("Ancien nom");

        mockMvc.perform(put("/api/garages/{id}", id).contentType(JSON).content(garageJson("Nouveau nom")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nouveau nom"));

        mockMvc.perform(get("/api/garages/{id}", id))
                .andExpect(jsonPath("$.name").value("Nouveau nom"));
    }

    @Test
    void shouldReturn404WhenUpdatingUnknownGarage() throws Exception {
        mockMvc.perform(put("/api/garages/{id}", 999_999).contentType(JSON).content(garageJson("X")))
                .andExpect(status().isNotFound());
    }

     // suppression
    @Test
    void shouldDeleteGarageAndItsVehiclesAndAccessories() throws Exception {
        long garageId = createGarage("Garage a supprimer");
        long vehicleId = createVehicle(garageId, "Clio", "PETROL");
        createAccessory(vehicleId, "GPS");

        mockMvc.perform(delete("/api/garages/{id}", garageId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/garages/{id}", garageId)).andExpect(status().isNotFound());
        assertThat(vehicleRepository.count()).isZero();
        assertThat(accessoryRepository.count()).isZero();
    }

    @Test
    void shouldReturn404WhenDeletingUnknownGarage() throws Exception {
        mockMvc.perform(delete("/api/garages/{id}", 999_999))
                .andExpect(status().isNotFound());
    }

    //recherche

    @Test
    void shouldSearchGaragesByFuelTypeAndAccessory() throws Exception {
        long electricGarage = createGarage("Garage electrique");
        long electricVehicle = createVehicle(electricGarage, "Zoe", "ELECTRIC");
        createAccessory(electricVehicle, "GPS");

        long petrolGarage = createGarage("Garage essence");
        long petrolVehicle = createVehicle(petrolGarage, "Clio", "PETROL");
        createAccessory(petrolVehicle, "Barres de toit");

        // sans critere : tous les garages
        mockMvc.perform(get("/api/garages/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_elements").value(2));

        // par type de carburant
        mockMvc.perform(get("/api/garages/search?fuelType=ELECTRIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_elements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Garage electrique"));

        // par accessoire
        mockMvc.perform(get("/api/garages/search?accessory=gps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_elements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Garage electrique"));

        // les deux critères combinés
        mockMvc.perform(get("/api/garages/search?fuelType=ELECTRIC&accessory=GPS"))
                .andExpect(jsonPath("$.total_elements").value(1));

        // aucun garage n'a un véhicule essence avec un GPS
        mockMvc.perform(get("/api/garages/search?fuelType=PETROL&accessory=GPS"))
                .andExpect(jsonPath("$.total_elements").value(0))
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void shouldReturn400WhenFuelTypeIsUnknown() throws Exception {
        mockMvc.perform(get("/api/garages/search?fuelType=UNKNOWN"))
                .andExpect(status().isBadRequest());
    }
}