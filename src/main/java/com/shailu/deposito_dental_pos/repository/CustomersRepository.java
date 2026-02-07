package com.shailu.deposito_dental_pos.repository;



import com.shailu.deposito_dental_pos.model.entity.Customers;
import com.shailu.deposito_dental_pos.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CustomersRepository extends JpaRepository<Customers, Long> {

    List<Customers> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    // Find all active products
    Page<Customers> findByDeleteDateIsNull(Pageable pageable);

    // Find by name
    Page<Customers> findByDeleteDateIsNullAndFirstNameContainingIgnoreCase(
            String firstName, Pageable pageable);


    @Query("SELECT COALESCE(MAX(c.id), 0) + 1 FROM Customers c")
    Long getNextSequence();

}
