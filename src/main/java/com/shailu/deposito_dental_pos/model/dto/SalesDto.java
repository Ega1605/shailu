package com.shailu.deposito_dental_pos.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesDto {

    private String code;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private Double subtotal;

    public SalesDto(String code, String name, String description, double price, int quantity) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = price * quantity;
    }
}
