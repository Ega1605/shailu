package com.shailu.deposito_dental_pos.model.enums;

public enum AccountStatus {

    PENDING("PENDIENTE"),
    PAID("PAGADO"),
    OVERDUE("VENCIDO");

    private final String accountStatus;

    AccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAccountStatus() {
        return accountStatus;
    }
}
