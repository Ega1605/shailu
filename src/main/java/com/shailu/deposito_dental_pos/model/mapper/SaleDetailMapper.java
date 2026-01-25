package com.shailu.deposito_dental_pos.model.mapper;

import com.shailu.deposito_dental_pos.model.dto.SaleDetailsDto;
import com.shailu.deposito_dental_pos.model.entity.Sales;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleDetailMapper {

    @Mapping(target = "paymentType", expression = "java(entity.getPaymentType().toString())")
    @Mapping(source = "entity.id", target = "folio")
    @Mapping(target = "customerName", expression = "java(entity.getCustomer().getFirstName() + \" \" + entity.getCustomer().getLastName())")
    SaleDetailsDto entityToDto(Sales entity);
}
