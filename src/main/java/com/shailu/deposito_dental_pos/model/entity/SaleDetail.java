package com.shailu.deposito_dental_pos.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sale_details")
public class SaleDetail extends OnlyDatesBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sales sales; //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 12)
    private Double unitPrice;

    @Column(name = "item_discount", precision = 12)
    private Double itemDiscount = 0d;

    @Column(name = "item_subtotal", nullable = false, precision = 12)
    private Double itemSubtotal;
}
