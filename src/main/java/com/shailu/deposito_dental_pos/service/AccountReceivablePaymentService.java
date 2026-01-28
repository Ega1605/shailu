package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.entity.AccountReceivable;
import com.shailu.deposito_dental_pos.model.entity.AccountReceivablePayment;
import com.shailu.deposito_dental_pos.model.enums.AccountStatus;
import com.shailu.deposito_dental_pos.model.enums.PaymentType;
import com.shailu.deposito_dental_pos.repository.AccountReceivablePaymentRepository;
import com.shailu.deposito_dental_pos.repository.AccountReceivableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AccountReceivablePaymentService {

    @Autowired
    private AccountReceivablePaymentRepository accountReceivablePaymentRepository;

    @Autowired
    private AccountReceivableRepository accountReceivableRepository;


    public void savePayment(AccountReceivable account,
                            Double amount,
                            PaymentType paymentType
    ) {

        // Save Payment
        AccountReceivablePayment payment = new AccountReceivablePayment();
        payment.setAccountReceivable(account);
        payment.setAmount(amount);
        payment.setPaymentType(paymentType);

        accountReceivablePaymentRepository.save(payment);

        // update amounts
        Double paid = account.getPaidAmount() + amount ;
        Double remaining = account.getTotalAmount() - paid;

        account.setPaidAmount(paid);
        account.setRemainingBalance(remaining);

        // update status
        if (remaining <= 0) {
            account.setStatus(AccountStatus.PAID.getAccountStatus());
            account.setPaidAt(LocalDateTime.now());
        }

        accountReceivableRepository.save(account);
    }
}
