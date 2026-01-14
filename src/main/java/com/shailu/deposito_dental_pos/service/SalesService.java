package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.dto.CurrentSaleDto;
import com.shailu.deposito_dental_pos.model.dto.SalesDto;
import com.shailu.deposito_dental_pos.model.entity.*;
import com.shailu.deposito_dental_pos.model.enums.MovementReason;
import com.shailu.deposito_dental_pos.model.enums.MovementType;
import com.shailu.deposito_dental_pos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void processSale(CurrentSaleDto currentSaleDto, String currentUser, Long customerId) {

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
