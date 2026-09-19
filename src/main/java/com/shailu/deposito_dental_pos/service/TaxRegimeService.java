package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.entity.TaxRegime;
import com.shailu.deposito_dental_pos.repository.TaxRegimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxRegimeService {

    @Autowired
    private TaxRegimeRepository taxRegimeRepository;


    public List<TaxRegime> findActive() {
        return taxRegimeRepository.findByIsActiveTrueOrderByCodeAsc();
    }

    public TaxRegime findActiveById(Long id) {
        return taxRegimeRepository
                .findByIdAndIsActiveTrue(id)
                .orElseThrow(() ->
                        new RuntimeException("Régimen fiscal no existe o está inactivo"));
    }

    public TaxRegime findByCode(Integer code) {
        return taxRegimeRepository
                .findByCode(code)
                .orElseThrow(() ->
                        new RuntimeException("Régimen fiscal no encontrado"));
    }


}
