package com.devminds.rentify.entity;

import com.devminds.rentify.enums.PaymentMethod;
import com.devminds.rentify.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payment")
public class Payment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @NotNull
    @Positive
    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @NotNull
    private LocalDateTime date;

    @ManyToOne
    private User owner;

    @ManyToOne
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "method")
    private PaymentMethod paymentMethod;

    @OneToOne
    private Rent rent;
}
