package src.convenience.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import src.convenience.dto.payment.PayRequest;
import src.convenience.service.paymentService.PaymentService;

@RestController
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @PostMapping("/api/pay")
    public ResponseEntity<?> pay(@RequestBody PayRequest request) {
        return ResponseEntity.ok(paymentService.pay(request));
    }

}
