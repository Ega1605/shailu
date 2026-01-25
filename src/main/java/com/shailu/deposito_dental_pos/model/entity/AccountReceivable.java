package com.shailu.deposito_dental_pos.model.entity;

import com.shailu.deposito_dental_pos.model.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts_receivable")
public class AccountReceivable extends OnlyDatesBaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sales sales;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customers customers;

    @Column(name = "total_amount", nullable = false, precision = 12)
    private Double totalAmount;

    @Column(name = "paid_amount", precision = 12)
    private Double paidAmount = 0.0;

    @Column(name = "remaining_balance", nullable = false, precision = 12)
    private Double remainingBalance;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "status")
    private String status = "PENDING"; // PENDING, PAID, OVERDUE

    @Column(name = "days_overdue")
    private Integer daysOverdue = 0;


    @PrePersist
    @PreUpdate
    public void calculateBalance() {
        if (totalAmount != null && paidAmount != null) {
            this.remainingBalance = totalAmount - paidAmount;

            if (this.remainingBalance  <= 0) {
                this.status = AccountStatus.PAID.getAccountStatus();
                if (this.paidAt == null) this.paidAt = LocalDateTime.now();
            }
        }
    }
}
