package com.shailu.deposito_dental_pos.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tax_regimes")
@Getter
@Setter
public class TaxRegime extends OnlyDatesBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer code;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "taxRegime", fetch = FetchType.LAZY)
    private List<Customers> customers;

}
