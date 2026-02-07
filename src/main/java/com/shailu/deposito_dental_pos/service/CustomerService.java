package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.dto.CustomerDto;
import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.entity.Customers;
import com.shailu.deposito_dental_pos.model.entity.Product;
import com.shailu.deposito_dental_pos.model.entity.TaxRegime;
import com.shailu.deposito_dental_pos.model.mapper.CustomerMapper;
import com.shailu.deposito_dental_pos.repository.CustomersRepository;
import com.shailu.deposito_dental_pos.repository.TaxRegimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private TaxRegimeRepository taxRegimeRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Transactional(readOnly = true)
    public List<CustomerDto> findByName(String name) {

        if (name == null || name.isBlank()) return Collections.emptyList();

        List<Customers> customers = customersRepository.
                findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name.trim(), name.trim());

        return customerMapper.convertListEntityToListDto(customers);
    }

    @Transactional(readOnly = true)
    public Page<CustomerDto> findPaginated(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customers> productPage;

        if (filter == null || filter.isEmpty()) {
            productPage = customersRepository.findByDeleteDateIsNull(pageable);
        } else {
            productPage = customersRepository
                    .findByDeleteDateIsNullAndFirstNameContainingIgnoreCase(filter, pageable);
        }

        return productPage.map(customerMapper::entityToDto);
    }

    public void addCustomer(CustomerDto customerDto){

        Customers customer = customerMapper.dtoToEntity(customerDto);
        TaxRegime taxRegime = getTaxRegime(customerDto.getTaxRegimeId());
        customer.setTaxRegime(taxRegime);
        customer.setCode(generateCustomerCode());

        customersRepository.save(customer);
    }
    private String generateCustomerCode() {
        Long nextId = customersRepository.getNextSequence(); // o count + 1
        return "SHAI-" + String.format("%05d", nextId);
    }

    public void updateCustomer(CustomerDto customerDto){

        Customers customer = customersRepository
                .findById(customerDto.getId())
                .orElseThrow();

        TaxRegime taxRegime = getTaxRegime(customerDto.getTaxRegimeId());

        customer.setTaxRegime(taxRegime);
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setReasonSocial(customerDto.getReasonSocial());
        customer.setRfc(customerDto.getRfc());

        customer.setAddress(customerDto.getAddress());
        customer.setPhone(customerDto.getPhone());
        customer.setEmail(customerDto.getEmail());
        customer.setZipCode(customerDto.getZipCode());

        customer.setCreditLimit(customerDto.getCreditLimit());
        customer.setCreditDays(customerDto.getCreditDays());
        customer.setSpecialDiscount(customerDto.getSpecialDiscount());

        customer.setIsActive(customerDto.getIsActive());

        customer.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));

        customersRepository.save(customer);
    }

    private TaxRegime getTaxRegime(Long id){
        TaxRegime taxRegime = taxRegimeRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Tax Regime not found"));

        return  taxRegime;
    }



}
