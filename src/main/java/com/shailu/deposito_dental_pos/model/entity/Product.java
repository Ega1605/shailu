package com.shailu.deposito_dental_pos.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product extends OnlyDatesBaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "barcode", nullable = false, unique = true)
    private String barCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    @Column(name = "purchase_price", nullable = false)
    private Double purchasePrice;

    @Column(name = "tax")
    private Double tax;

    @Column(name = "profit")
    private Double profit;

    @Column(name = "current_stock")
    private int currentStock;

    @Column(name = "minimum_stock")
    private int minimumStock;

    @Column(name = "maximum_stock")
    private int maximumStock;

}
