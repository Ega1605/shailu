package com.shailu.deposito_dental_pos.repository;

import com.shailu.deposito_dental_pos.model.entity.TaxRegime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaxRegimeRepository extends JpaRepository<TaxRegime, Long> {


    List<TaxRegime> findByIsActiveTrueOrderByCodeAsc();


    Optional<TaxRegime> findByCode(Integer code);


    Optional<TaxRegime> findByIdAndIsActiveTrue(Long id);
}
