package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.dto.SaleDetailsDto;
import com.shailu.deposito_dental_pos.model.dto.SaleFilterDto;
import com.shailu.deposito_dental_pos.model.entity.SaleDetail;
import com.shailu.deposito_dental_pos.model.entity.Sales;
import com.shailu.deposito_dental_pos.model.enums.SaleStatus;
import com.shailu.deposito_dental_pos.model.mapper.SaleDetailMapper;
import com.shailu.deposito_dental_pos.repository.ProductRepository;
import com.shailu.deposito_dental_pos.repository.SaleDetailRepository;
import com.shailu.deposito_dental_pos.repository.SalesRepository;
import com.shailu.deposito_dental_pos.repository.SalesSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class SaleDetailsService {

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private SaleDetailRepository saleDetailRepository;

    @Autowired
    private SaleDetailMapper saleDetailMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SalesService salesService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());



    public Page<SaleDetailsDto> findPaginated(SaleFilterDto filter,
                                              int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdDate")
        );

        Specification<Sales> specification =
                SalesSpecification.filter(filter);

        return salesRepository
                .findAll(specification, pageable)
                .map(saleDetailMapper::entityToDto);

    }

    @Transactional(readOnly = true)
    public List<SaleDetail> findItemsBySaleId(Long saleId) {
        return saleDetailRepository.findBySaleIdWithProduct(saleId);
    }

    @Transactional
    public void cancelSale(Long saleId){


        Sales sale = salesService.findSale(saleId);

        if (SaleStatus.CANCELLED.getSaleStatus().equalsIgnoreCase(sale.getStatus().getSaleStatus())) {
            throw new RuntimeException("LA VENTA YA ESTA CANCELADA");
        }

        restoreProductsInStock(saleId);

        sale.setStatus(SaleStatus.CANCELLED);
        sale.setNotes(sale.getNotes() + " [Canelada POR CORRECCIÓN EL " + LocalDateTime.now() + "]");

        salesRepository.save(sale);

    }

    public void restoreProductsInStock(Long saleId) {
        List<SaleDetail> saleDetails = saleDetailRepository.findBySaleIdWithProduct(saleId);

        productService.restoreProductsInStock(saleDetails);
    }

    public void deleteDetailsBySaleId(Long saleId){
        saleDetailRepository.deleteBySaleId(saleId);
    }

}
