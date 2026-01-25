package com.shailu.deposito_dental_pos.repository;

import com.shailu.deposito_dental_pos.model.entity.AccountReceivable;
import com.shailu.deposito_dental_pos.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountReceivableRepository extends JpaRepository<AccountReceivable, Long> {

    Optional<AccountReceivable> findBySales_Id(Long saleId);
}
