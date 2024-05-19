package com.ssg.dsilbackend.domain;

import com.ssg.dsilbackend.dto.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "name")
    private String name;

    @Column(name = "pg")
    private String pg;

    @Column(name = "payment_time", nullable = false)
    private LocalDateTime paymentTime;

    @Column(name = "pay_method", nullable = false)
    private String payMethod;

    @Column(name = "merchant_uid")
    private String merchantUid;

    @Column(name = "buyer_email")
    private String buyerEmail;

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "buyer_tel")
    private String buyerTel;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "point_usage")
    private Long pointUsage;

    public void cancelPaymentStatus(PaymentStatus paymentStatus){
        this.paymentStatus = paymentStatus;
    }
}
