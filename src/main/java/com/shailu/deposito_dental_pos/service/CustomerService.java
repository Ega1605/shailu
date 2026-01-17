package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.dto.CustomerDto;
import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.entity.Customers;
import com.shailu.deposito_dental_pos.model.mapper.CustomerMapper;
import com.shailu.deposito_dental_pos.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Transactional(readOnly = true)
    public List<CustomerDto> findByName(String name) {

        if (name == null || name.isBlank()) return Collections.emptyList();

        List<Customers> customers = customersRepository.
                findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name.trim(), name.trim());

        List<CustomerDto> list = customerMapper.convertListEntityToListDto(customers);

        return list;
    }
}
