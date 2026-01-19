package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.entity.Product;
import com.shailu.deposito_dental_pos.model.mapper.ProductMapper;
import com.shailu.deposito_dental_pos.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public static final String DEFAULT_UNIT_OF_MEASURE = "Unit";
    public static final int DEFAULT_MINIMUM_STOCK = 5;

    public Optional<ProductDto> findByBarCode(String barCode) {
        return productRepository.findByBarCode(barCode)
                .map(productMapper::entityToDto);
    }

    public Optional<ProductDto> findByCode(String code) {
        return productRepository.findByCode(code)
                .map(productMapper::entityToDto);
    }

    public List<ProductDto> searchProductsByName(String description) {
        return productMapper.convertListEntityToListDto(
                productRepository.findByNameContainingIgnoreCaseOrderByNameAsc(description)
        );
    }

    public void addProduct(ProductDto productDto){

        Optional<Product> product = productRepository.findByCode(productDto.getCode());

        if(product.isPresent()){
            addProductStock(product.get(), productDto.getQuantity());
        } else {

            productDto.setUnitOfMeasure(DEFAULT_UNIT_OF_MEASURE);
            productDto.setCurrentStock(productDto.getQuantity());
            productDto.setMinimumStock(DEFAULT_MINIMUM_STOCK);
            Product newProduct = productMapper.dtoToEntity(productDto);

            productRepository.save(newProduct);
        }

    }

    private void addProductStock(Product product, int quantity){

        product.setCurrentStock(product.getCurrentStock() + quantity);

        productRepository.save(product);

    }

    public Page<ProductDto> findPaginated(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;

        if (filter == null || filter.isEmpty()) {
            productPage = productRepository.findByDeleteDateIsNull(pageable);
        } else {
            productPage = productRepository
                    .findByDeleteDateIsNullAndNameContainingIgnoreCaseOrDeleteDateIsNullAndCodeContainingIgnoreCase(filter, filter, pageable);
        }

        return productPage.map(productMapper::entityToDto);
    }

    public void deleteById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setDeleteDate(Timestamp.valueOf(LocalDateTime.now()));
        productRepository.save(product);
    }

}
