package com.shailu.deposito_dental_pos.repository;

import com.shailu.deposito_dental_pos.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByBarCode(String barCode);
    Optional<Product> findByName(String name);
    Optional<Product> findByCode(String code);
    Page<Product> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code, Pageable pageable);
    List<Product> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
}
