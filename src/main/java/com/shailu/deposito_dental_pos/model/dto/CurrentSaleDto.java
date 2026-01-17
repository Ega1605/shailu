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
    PaymentType paymentType;
    SaleStatus status;
    String notes;
    int customerId;
    int sellerId;
    List<SalesDto> items;
    Double subtotal;
    Double generalDiscount ;
    Double taxAmount;
    Double total;

}
