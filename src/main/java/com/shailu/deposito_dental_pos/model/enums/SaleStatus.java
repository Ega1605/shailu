package com.shailu.deposito_dental_pos.model.enums;

public enum SaleStatus {

    COMPLETED("Completado"),
    PENDING("Pendientes"),
    CANCELLED("Cancelado"),
    UPDATED("Editado"),
    QUOTATION("Cotización")
    ;

    private final String saleStatus;

    SaleStatus(String saleStatus) {
        this.saleStatus = saleStatus;
    }

    public String getSaleStatus() {
        return saleStatus;
    }

    @Override
    public String toString() {
        return saleStatus;
    }
}
