package com.shailu.deposito_dental_pos.model.mapper;

import com.shailu.deposito_dental_pos.model.dto.CustomerDto;
import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.entity.Customers;
import com.shailu.deposito_dental_pos.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "taxRegime", ignore = true)
    Customers dtoToEntity (CustomerDto dto);

    @Mapping(source = "taxRegime.id", target = "taxRegimeId")
    @Mapping(source = "taxRegime.description", target = "taxRegimeLabel")
    CustomerDto entityToDto(Customers entity);

    List<CustomerDto> convertListEntityToListDto(List<Customers> entityList);

}
