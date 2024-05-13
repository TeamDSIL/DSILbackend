package com.ssg.dsilbackend.dto.payment;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentDTO {
    private String pg;
    private String pay_method;
    private String merchant_uid;
    private String name;
    private Long amount;
    private LocalDateTime paymentTime;
    private String merchantUid;
    private String buyerEmail;
    private String buyerName;
    private String buyerTel;
    private Long reservationId;
}
