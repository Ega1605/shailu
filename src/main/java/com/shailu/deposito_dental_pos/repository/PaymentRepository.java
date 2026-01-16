package com.shailu.deposito_dental_pos.repository;


import com.shailu.deposito_dental_pos.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
