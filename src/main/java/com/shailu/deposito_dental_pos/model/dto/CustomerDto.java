package com.shailu.deposito_dental_pos.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto {

    private Long id;

    private String code;

    private String taxRegime;

    private String firstName;

    private String lastName;

    private String reasonSocial;

    private String rfc;

    private String address;

    private String phone;

    private String email;

    private String zipCode;

    private Double creditLimit = 0d;

    private Integer creditDays = 0;

    private Double specialDiscount = 0d;

    private Boolean isActive = true;
}
