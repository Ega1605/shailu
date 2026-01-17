package com.shailu.deposito_dental_pos.model.enums;

public enum PaymentType {

    CASH("Efectivo"),
    CREDIT_CARD("Tarjeta de crédito"),
    DEBIT_CARD("Tarjeta de débito"),
    TRANSFER("Transferencia"),
    CREDIT("Linea de Crédito");

    private final String paymentType;

    PaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    @Override
    public String toString() {
        return paymentType;
    }
}
