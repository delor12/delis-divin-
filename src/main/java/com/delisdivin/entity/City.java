package com.delisdivin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cities")
@Getter
@Setter
public class City extends AbstractAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private boolean active = true;
}
