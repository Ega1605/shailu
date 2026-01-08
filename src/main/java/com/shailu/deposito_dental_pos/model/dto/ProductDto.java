package com.shailu.deposito_dental_pos.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

    private Long id;
    private String code;

    private String barCode;

    private String name;

    private String description;

    private String unitOfMeasure;

    private Double purchasePrice;

    private Double tax;

    private Double profit;

    private Double price;

    private int currentStock;

    private int minimumStock;

    private int maximumStock;
}
