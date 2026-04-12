package com.shailu.deposito_dental_pos.model.mapper;

import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "dto.barCode", target = "barCode")
    Product dtoToEntity (ProductDto dto);

    @Mapping(
            target = "price",
            expression = "java(calculatePrice(entity))"
    )
    ProductDto entityToDto(Product entity);

    List<ProductDto> convertListEntityToListDto(List<Product> entityList);


    default Double calculatePrice(Product entity) {

        if (entity.getPurchasePrice() == null ||
                entity.getProfit() == null) {
            return 0.0;
        }

        double subtotal = entity.getPurchasePrice() * (1 + (entity.getProfit() / 100));

        return BigDecimal.valueOf(subtotal)
                .setScale(0, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
