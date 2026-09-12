package com.shailu.deposito_dental_pos.repository;

import com.shailu.deposito_dental_pos.model.dto.SaleFilterDto;
import com.shailu.deposito_dental_pos.model.entity.Customers;
import com.shailu.deposito_dental_pos.model.entity.Sales;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class SalesSpecification {

    public static Specification<Sales> filter(SaleFilterDto filter) {

        return (root, query, cb) -> {

            Predicate predicate = cb.conjunction();

            if (filter.getSaleId() != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("id"), filter.getSaleId())
                );
            }

            if (filter.getCustomerName() != null &&
                    !filter.getCustomerName().isBlank()) {

                Join<Sales, Customers> customer = root.join("customer");

                var fullName = cb.concat(
                        cb.concat(
                                customer.get("firstName"),
                                " "
                        ),
                        customer.get("lastName")
                );

                predicate = cb.and(
                        predicate,
                        cb.like(
                                cb.lower(fullName),
                                "%" + filter.getCustomerName().toLowerCase() + "%"
                        )
                );
            }

            if (filter.getCreatedDate() != null) {

                LocalDateTime start = filter.getCreatedDate().atStartOfDay();
                LocalDateTime end = filter.getCreatedDate().atTime(LocalTime.MAX);

                predicate = cb.and(
                        predicate,
                        cb.between(root.get("createdDate"), start, end)
                );
            }

            return predicate;
        };
    }
}
