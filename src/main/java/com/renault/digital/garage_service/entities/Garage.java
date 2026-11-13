package com.renault.digital.garage_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="garage")
@Setter
@Getter
@NoArgsConstructor @AllArgsConstructor
public class Garage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private  String email;

    @OneToMany(mappedBy = "garage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "garage_opening_time", joinColumns = @JoinColumn(name = "garage_id"))
    private List<OpeningTime> openingTimes = new ArrayList<>();
}
