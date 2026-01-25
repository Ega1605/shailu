package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.dto.SaleDetailsDto;
import com.shailu.deposito_dental_pos.model.entity.SaleDetail;
import com.shailu.deposito_dental_pos.model.entity.Sales;
import com.shailu.deposito_dental_pos.model.mapper.SaleDetailMapper;
import com.shailu.deposito_dental_pos.repository.SaleDetailRepository;
import com.shailu.deposito_dental_pos.repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SaleDetailsService {

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private SaleDetailRepository saleDetailRepository;

    @Autowired
    private SaleDetailMapper saleDetailMapper;


    public Page<SaleDetailsDto> findPaginated(Long filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Sales> productPage;

        if (filter == null) {
            productPage = salesRepository.findByDeleteDateIsNull(pageable);
        } else {
            productPage = salesRepository.findByDeleteDateIsNullAndId(filter, pageable);
        }

        return productPage.map(saleDetailMapper::entityToDto);
    }

    @Transactional(readOnly = true)
    public List<SaleDetail> findItemsBySaleId(Long saleId) {
        return saleDetailRepository.findBySaleIdWithProduct(saleId);
    }
}
