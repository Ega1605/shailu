package com.shailu.deposito_dental_pos.repository;


import com.shailu.deposito_dental_pos.model.entity.InventoryMovements;
import com.shailu.deposito_dental_pos.model.projection.ProductOutProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryMovementsRepository extends JpaRepository<InventoryMovements, Long> {

    @Query("""
        SELECT p.code AS code,
            p.name AS name,
            SUM(im.quantity) AS totalQuantity
        FROM InventoryMovements im
        JOIN im.product p
        WHERE im.movementType = 'Fuera'
          AND im.createdDate BETWEEN :start AND :end
        GROUP BY p.code, p.name
    """)
    List<ProductOutProjection> getDailyProductOut(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
