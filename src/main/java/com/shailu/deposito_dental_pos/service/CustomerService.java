package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.dto.CustomerDto;
import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.entity.Customers;
import com.shailu.deposito_dental_pos.model.mapper.CustomerMapper;
import com.shailu.deposito_dental_pos.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustomerMapper customerMapper;

    public List<CustomerDto> findByName(String name) {

        if (name == null || name.isBlank()) return Collections.emptyList();

        List<Customers> customers = customersRepository.
                findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name.trim(), name.trim());

        return customerMapper.convertListEntityToListDto(customers);

    }
}
