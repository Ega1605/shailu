package com.shailu.deposito_dental_pos.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "customers")
public class Customers extends OnlyDatesBaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_regime_id", nullable = false)
    private TaxRegime taxRegime;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "reason_social", length = 200)
    private String reasonSocial;

    @Column(nullable = false, length = 20)
    private String rfc;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(name = "zip_code", length = 100)
    private String zipCode;

    @Column(name = "credit_limit", precision = 12)
    private Double creditLimit = 0d;

    @Column(name = "credit_days")
    private Integer creditDays = 0;

    @Column(name = "special_discount", precision = 5)
    private Double specialDiscount = 0d;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
