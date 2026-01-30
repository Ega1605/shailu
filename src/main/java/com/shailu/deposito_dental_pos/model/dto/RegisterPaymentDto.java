package com.shailu.deposito_dental_pos.model.dto;

import com.shailu.deposito_dental_pos.model.enums.PaymentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterPaymentDto {

    private Double amount;

    private PaymentType paymentType;

    public RegisterPaymentDto(Double amount, PaymentType paymentType) {
        this.amount = amount;
        this.paymentType = paymentType;
    }
}
