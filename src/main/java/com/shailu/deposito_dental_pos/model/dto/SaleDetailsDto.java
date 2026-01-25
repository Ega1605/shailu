package com.shailu.deposito_dental_pos.model.dto;

import com.shailu.deposito_dental_pos.model.enums.PaymentType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class SaleDetailsDto {


    private Long folio;

    private String customerName;

    private Double total;

    private Timestamp createdDate;

    private String paymentType;

}
