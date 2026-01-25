package com.shailu.deposito_dental_pos.repository;

import com.shailu.deposito_dental_pos.model.entity.Sales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {

    // Find all active Sales
    @Query("""
            SELECT s
            FROM Sales s
            JOIN FETCH s.customer
            WHERE s.deleteDate IS NULL
            """)
    Page<Sales> findByDeleteDateIsNull(Pageable pageable);

    // Find by id or code if they are active
    @Query("""
                SELECT s
                FROM Sales s
                JOIN FETCH s.customer
                WHERE s.deleteDate IS NULL
                AND s.id = :id
            """)
    Page<Sales> findByDeleteDateIsNullAndId(
            Long id, Pageable pageable);
}
