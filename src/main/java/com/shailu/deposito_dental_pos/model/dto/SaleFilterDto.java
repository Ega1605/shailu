package com.shailu.deposito_dental_pos.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SaleFilterDto {

    private Long saleId;

    private String customerName;

    private LocalDate createdDate;
}
