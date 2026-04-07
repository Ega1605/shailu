package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.dto.SaleDetailsDto;
import com.shailu.deposito_dental_pos.model.entity.Product;
import com.shailu.deposito_dental_pos.model.entity.SaleDetail;
import com.shailu.deposito_dental_pos.model.entity.Sales;
import com.shailu.deposito_dental_pos.model.enums.SaleStatus;
import com.shailu.deposito_dental_pos.model.mapper.SaleDetailMapper;
import com.shailu.deposito_dental_pos.repository.ProductRepository;
import com.shailu.deposito_dental_pos.repository.SaleDetailRepository;
import com.shailu.deposito_dental_pos.repository.SalesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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



    public Page<SaleDetailsDto> findPaginated(Long filter,LocalDate dateFilter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Sales> productPage;

        if (filter != null) {

            productPage = salesRepository.findSalesById(filter, pageable);
        } else if(dateFilter != null){

            LocalDateTime start = dateFilter.atStartOfDay(); // 00:00:00
            LocalDateTime end = dateFilter.atTime(LocalTime.MAX); // 23:59:59
            productPage = salesRepository.findByCreatedDateBetween(start, end, pageable);
        } else {

            productPage = salesRepository.findAllSales(pageable);
        }

        return productPage.map(saleDetailMapper::entityToDto);
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

        List<SaleDetail> saleDetails = saleDetailRepository.findBySaleIdWithProduct(saleId);

        for(SaleDetail productSale : saleDetails){
            Product product = productService.findProductById(productSale.getProduct().getId());

            int newStock = product.getCurrentStock() + productSale.getQuantity();

            product.setCurrentStock(newStock);

            productRepository.save(product);

            logger.info("Restaurado stock de {}: +{} unidades. Stock actual: {}",
                    product.getName(),  productSale.getQuantity(), newStock);

        }

        sale.setStatus(SaleStatus.CANCELLED);
        sale.setNotes(sale.getNotes() + " [Canelada POR CORRECCIÓN EL " + LocalDateTime.now() + "]");

        salesRepository.save(sale);

    }

}
