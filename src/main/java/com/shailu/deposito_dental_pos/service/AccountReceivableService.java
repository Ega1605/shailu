package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.entity.AccountReceivable;
import com.shailu.deposito_dental_pos.repository.AccountReceivableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountReceivableService {

    @Autowired
    private AccountReceivableRepository accountReceivableRepository;

    public AccountReceivable findBySaleId(Long saleId){
        return accountReceivableRepository.findBySales_Id(saleId)
                .orElseThrow(() -> new RuntimeException("Account Receivable not found"));
    }

    public AccountReceivable registerPayment(Long saleId, Double amountPaid) {

        AccountReceivable ar = accountReceivableRepository.findBySales_Id(saleId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        ar.setPaidAmount(ar.getPaidAmount() + amountPaid);

        // remainingBalance, status y paidAt

        return accountReceivableRepository.save(ar);
    }
}
