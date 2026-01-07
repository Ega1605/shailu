package com.shailu.deposito_dental_pos.model.mapper;

import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product dtoToEntity (ProductDto dto);

    @Mapping(
            target = "price",
            expression = "java(calculatePrice(entity))"
    )
    ProductDto entityToDto(Product entity);

    List<ProductDto> convertListEntityToListDto(List<Product> entityList);


    default Double calculatePrice(Product entity) {

        if (entity.getPurchasePrice() == null ||
                entity.getTax() == null ||
                entity.getProfit() == null) {
            return 0.0;
        }

        return (entity.getPurchasePrice() + (entity.getPurchasePrice()* entity.getTax())
                + entity.getProfit());
    }

}
