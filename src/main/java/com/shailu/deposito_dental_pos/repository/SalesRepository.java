package com.shailu.deposito_dental_pos.repository;

import com.shailu.deposito_dental_pos.model.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {
}
