package com.shailu.deposito_dental_pos.model.projection;

public interface SalesByPaymentProjection {
    String getPaymentType();
    Long getTotalSales();
    Double getTotalAmount();
}
