package com.shailu.deposito_dental_pos.repository;



import com.shailu.deposito_dental_pos.model.entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CustomersRepository extends JpaRepository<Customers, Long> {

    List<Customers> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}
