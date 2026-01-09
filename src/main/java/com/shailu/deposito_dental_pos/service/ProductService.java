package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.entity.Product;
import com.shailu.deposito_dental_pos.model.mapper.ProductMapper;
import com.shailu.deposito_dental_pos.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public static final int ADD_UNIT_TO_CURRENT_STOCK = 1;
    public static final String DEFAULT_UNIT_OF_MEASURE = "Unit";
    public static final int DEFAULT_MINIMUM_STOCK = 5;

    public Optional<ProductDto> findByBarCode(String barCode) {
        return productRepository.findByBarCode(barCode)
                .map(productMapper::entityToDto);
    }

    public Optional<ProductDto> searchProductByName(String barCode) {
        return productRepository.findByName(barCode)
                .map(productMapper::entityToDto);
    }

    public void addProduct(ProductDto productDto){

        Optional<Product> product = productRepository.findByBarCode(productDto.getBarCode());

        if(product.isPresent()){
            addProductStock(product.get());
        } else {

            productDto.setUnitOfMeasure(DEFAULT_UNIT_OF_MEASURE);
            productDto.setCurrentStock(ADD_UNIT_TO_CURRENT_STOCK);
            productDto.setMinimumStock(DEFAULT_MINIMUM_STOCK);
            Product newProduct = productMapper.dtoToEntity(productDto);

            productRepository.save(newProduct);
        }

    }

    private void addProductStock(Product product){

        product.setCurrentStock(product.getCurrentStock() + ADD_UNIT_TO_CURRENT_STOCK);

        productRepository.save(product);

    }

    public List<ProductDto> findAll() {
        return productMapper.convertListEntityToListDto(productRepository.findAll());
    }

}
