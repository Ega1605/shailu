package com.shailu.deposito_dental_pos.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesDto {

    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private Double subtotal;

    public SalesDto(String name, String description, double price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = price * quantity;
    }
}
