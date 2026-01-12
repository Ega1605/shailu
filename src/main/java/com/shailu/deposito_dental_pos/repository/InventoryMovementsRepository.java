package com.shailu.deposito_dental_pos.repository;


import com.shailu.deposito_dental_pos.model.entity.InventoryMovements;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementsRepository extends JpaRepository<InventoryMovements, Long> {
}
