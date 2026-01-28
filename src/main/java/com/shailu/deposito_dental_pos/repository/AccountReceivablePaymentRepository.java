package com.shailu.deposito_dental_pos.repository;

import com.shailu.deposito_dental_pos.model.entity.AccountReceivablePayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountReceivablePaymentRepository  extends JpaRepository<AccountReceivablePayment, Long> {

    List<AccountReceivablePayment>
    findByAccountReceivableIdOrderByCreatedDateAsc(Long accountReceivableId);
}
