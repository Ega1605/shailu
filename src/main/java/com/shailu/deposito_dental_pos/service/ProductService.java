package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.entity.Product;
import com.shailu.deposito_dental_pos.model.entity.SaleDetail;
import com.shailu.deposito_dental_pos.model.mapper.ProductMapper;
import com.shailu.deposito_dental_pos.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Optional<ProductDto> findByBarCode(String barCode) {
        return productRepository.findByBarCodeAndDeleteDateIsNull(barCode)
                .map(productMapper::entityToDto);
    }

    public Optional<ProductDto> findByCode(String code) {
        return productRepository.findByCodeAndDeleteDateIsNull(code)
                .map(productMapper::entityToDto);
    }

    public List<ProductDto> searchProductsByName(String description) {
        return productMapper.convertListEntityToListDto(
                productRepository.findByNameContainingIgnoreCaseAndDeleteDateIsNullOrderByNameAsc(description)
        );
    }

    public Product findProductById(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

    }

    public void addProduct(ProductDto productDto){

        Optional<Product> product = productRepository.findByCode(productDto.getCode());

        if(product.isPresent()){
            addProductStock(product.get(), productDto.getQuantity(), productDto);
        } else {

            productDto.setUnitOfMeasure(DEFAULT_UNIT_OF_MEASURE);
            productDto.setCurrentStock(productDto.getQuantity());
            productDto.setMinimumStock(DEFAULT_MINIMUM_STOCK);
            Product newProduct = productMapper.dtoToEntity(productDto);

            productRepository.save(newProduct);
        }

    }

    public void updateName(ProductDto productDto){

        Optional<Product> product = productRepository.findByCodeAndDeleteDateIsNull(productDto.getCode());

        if(product.isPresent()){

            Product productUpdated = product.get();
            productUpdated.setName(productDto.getName());
            productRepository.save(productUpdated);
        }
    }


    private void addProductStock(Product product, int quantity, ProductDto productDto){

        if (product.getDeleteDate() != null) {
            product.setDeleteDate(null);
            product.setCurrentStock(quantity);
        } else {

            product.setCurrentStock(product.getCurrentStock() + quantity);

        }
        product.setBarCode(productDto.getBarCode());
        product.setProfit(productDto.getProfit());
        product.setPurchasePrice(productDto.getPurchasePrice());
        product.setBarCode(productDto.getBarCode());

        productRepository.save(product);

    }

    private void updateCurrentStockFromTable(ProductDto productDto){
        Optional<Product> product = productRepository.findByCodeAndDeleteDateIsNull(productDto.getCode());

        if(product.isPresent()){

            Product productUpdated = product.get();
            productUpdated.setCurrentStock(productDto.getCurrentStock());
            productRepository.save(productUpdated);

        }

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

    public void restoreProductsInStock(List<SaleDetail> saleDetails) {

        for(SaleDetail productSale : saleDetails){
            Product product = this.findProductById(productSale.getProduct().getId());

            int newStock = product.getCurrentStock() + productSale.getQuantity();

            product.setCurrentStock(newStock);

            productRepository.save(product);

            logger.info("Restaurado stock de {}: +{} unidades. Stock actual: {}",
                    product.getName(),  productSale.getQuantity(), newStock);

        }
    }

}
