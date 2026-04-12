package com.shailu.deposito_dental_pos.model.dto;

import com.shailu.deposito_dental_pos.model.enums.PaymentType;
import com.shailu.deposito_dental_pos.model.enums.SaleStatus;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CurrentSaleDto extends SalesDto{
    private Long saleId;
    private PaymentType paymentType;
    private SaleStatus status;
    private String notes;
    private int customerId;
    private int sellerId;
    private List<SalesDto> items;
    private Double subtotal;
    private Double generalDiscount ;
    private Double taxAmount;
    private Double total;

}
