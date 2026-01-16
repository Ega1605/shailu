package com.shailu.deposito_dental_pos.model.mapper;

import com.shailu.deposito_dental_pos.model.dto.CustomerDto;
import com.shailu.deposito_dental_pos.model.entity.Customers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(source = "entity.taxRegime.description", target = "taxRegime")
    CustomerDto entityToDto(Customers entity);

    List<CustomerDto> convertListEntityToListDto(List<Customers> entityList);
}
