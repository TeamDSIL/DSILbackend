package com.ssg.dsilbackend.controller;

import com.ssg.dsilbackend.dto.payment.PaymentDTO;
import com.ssg.dsilbackend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/restaurant")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment")
    public ResponseEntity<?> savePayment(@RequestBody PaymentDTO paymentDTO,@RequestParam Long reservationId) {
        try {

            log.info("PaymentDTO:" + paymentDTO);
            paymentService.savePayment(paymentDTO,reservationId);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during save payment", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during save payment");
        }
    }

}
