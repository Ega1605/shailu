package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.dto.CurrentSaleDto;
import com.shailu.deposito_dental_pos.model.dto.SalesDto;
import com.shailu.deposito_dental_pos.model.entity.*;
import com.shailu.deposito_dental_pos.model.enums.*;
import com.shailu.deposito_dental_pos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SalesService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryMovementsRepository movementsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private SaleDetailRepository saleDetailRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AccountReceivableRepository accountReceivableRepository;

    @Transactional
    public Sales processSale(CurrentSaleDto currentSaleDto, String currentUser, Long customerId) {

        Long finalCustomerId = (customerId != null) ? customerId : 1L;

        Customers customer = customersRepository.findById(finalCustomerId)
                .orElseThrow(() -> new RuntimeException("Client does not exist"));

        User user = userRepository.findByUsername(currentUser).orElseThrow(() -> new RuntimeException("User not found"));

        for (SalesDto item : currentSaleDto.getItems()) {

            Product product = productRepository.findByCode(item.getCode())
                    .orElseThrow(() -> new RuntimeException("Product not found" + item.getCode()));

            int previousStock = product.getCurrentStock();
            int saleQuantity = item.getQuantity();
            int newStock = previousStock - saleQuantity;

            if (newStock < 0) {
                throw new RuntimeException("Stock is not enough for : " + product.getName());
            }

            //Update stock product
            product.setCurrentStock(newStock);
            productRepository.save(product);

            //Create inventory movement for product
            createInventoryMovements(product, saleQuantity, previousStock, newStock, user);
        }

        Sales sale = saveSale(currentSaleDto, customer, user);

        System.out.println("ID sale: " + sale.getId());

        saveSaleDetail(currentSaleDto, sale);

        savePayment(sale,user, currentSaleDto.getPaymentType());

        if(currentSaleDto.getPaymentType().equals(PaymentType.CREDIT)){
            saveAccountReceivable(sale, currentSaleDto, customer, user);
        }

        return sale;

    }

    private void saveAccountReceivable(Sales sale, CurrentSaleDto currentSaleDto, Customers customer, User user ){
        AccountReceivable accountReceivable = new AccountReceivable();
        accountReceivable.setSales(sale);
        accountReceivable.setCustomers(customer);
        accountReceivable.setTotalAmount(currentSaleDto.getTotal());
        accountReceivable.setPaidAmount(0.0);
        accountReceivable.setRemainingBalance(currentSaleDto.getTotal());
        accountReceivable.setDueDate(LocalDate.now().plusWeeks(1));
        accountReceivable.setStatus(AccountStatus.PENDING.getAccountStatus());
        accountReceivable.setDaysOverdue(0);

        accountReceivableRepository.save(accountReceivable);

    }

    private void savePayment(Sales sale, User user, PaymentType paymentType){

        Payment payment = new Payment();
        payment.setSale(sale);
        payment.setPaymentMethod(paymentType);
        payment.setAmount(sale.getTotal());
        payment.setUser(user);
        payment.setNotes(sale.getNotes());

        paymentRepository.save(payment);

    }

    private void saveSaleDetail(CurrentSaleDto currentSaleDto, Sales sale){

        for (SalesDto item : currentSaleDto.getItems()) {
            Product product = productRepository.findByCode(item.getCode())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getCode()));

            // Create Detail
            SaleDetail detail = new SaleDetail();
            detail.setSales(sale);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getPrice());
            detail.setItemSubtotal(item.getSubtotal());

            saleDetailRepository.save(detail);
        }

    }

    private Sales saveSale(CurrentSaleDto currentSaleDto, Customers customer, User user){

        Sales sale = new Sales();

        sale.setCustomer(customer);
        sale.setSeller(user);
        sale.setTotal(currentSaleDto.getTotal());
        sale.setPaymentType(currentSaleDto.getPaymentType());
        sale.setStatus(currentSaleDto.getStatus());
        sale.setNotes(currentSaleDto.getNotes());

        return salesRepository.save(sale);
    }

    private void createInventoryMovements(Product product, int saleQuantity, int previousStock, int newStock,
                                          User currentUser){
        InventoryMovements movement = new InventoryMovements();
        movement.setProduct(product);
        movement.setMovementType(MovementType.OUT.getMovementType());
        movement.setReason(MovementReason.SALE.getReason());
        movement.setQuantity(saleQuantity);
        movement.setPreviousStock(previousStock);
        movement.setCurrentStock(newStock);
        movement.setUnitPrice(product.getPurchasePrice());
        movement.setUser(currentUser);
        movement.setNotes("Venta de mostrador");

        movementsRepository.save(movement);
    }


}
