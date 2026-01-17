package com.shailu.deposito_dental_pos.model.enums;

public enum SaleStatus {

    COMPLETED("Completado"),
    PENDING("Pendientes"),
    CANCELLED("Cancelado");

    private final String saleStatus;

    SaleStatus(String saleStatus) {
        this.saleStatus = saleStatus;
    }

    public String getSaleStatus() {
        return saleStatus;
    }
}
