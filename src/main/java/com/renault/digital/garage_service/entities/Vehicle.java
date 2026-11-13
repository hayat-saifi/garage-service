package com.renault.digital.garage_service.entities;

import com.renault.digital.garage_service.enums.FuelType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Un véhicule appartient à un seul garage. Un même modèle
 * peut donc exister dans plusieurs garages : il y aura plusieurs lignes Vehicle.
 */
@Entity
@Table(name="vehicle")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(name = "annee_fabrication", nullable = false)
    private Integer anneeFabrication;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_carburant", nullable = false)
    private FuelType typeCarburant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "garage_id", nullable = false)
    private Garage garage;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Accessory> accessories = new ArrayList<>();
}
