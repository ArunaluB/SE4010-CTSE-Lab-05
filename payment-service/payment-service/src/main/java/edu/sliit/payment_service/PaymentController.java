package edu.sliit.payment_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private List<Map<String, Object>> payments = new ArrayList<>();
    private int idCounter = 1;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllPayments() {
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable int id) {
        Optional<Map<String, Object>> payment = payments.stream()
                .filter(p -> p.get("id").equals(id))
                .findFirst();

        if (payment.isPresent()) {
            return ResponseEntity.ok(payment.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Payment not found with id: " + id));
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody Map<String, Object> payment) {
        payment.put("id", idCounter++);
        payment.put("status", "SUCCESS");
        payment.put("processedAt", LocalDateTime.now().toString());
        payments.add(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Map<String, Object>>> getPaymentsByOrderId(@PathVariable int orderId) {
        List<Map<String, Object>> orderPayments = payments.stream()
                .filter(p -> {
                    Object oid = p.get("orderId");
                    if (oid instanceof Integer) return oid.equals(orderId);
                    return false;
                })
                .toList();
        return ResponseEntity.ok(orderPayments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable int id) {
        boolean removed = payments.removeIf(p -> p.get("id").equals(id));
        if (removed) {
            return ResponseEntity.ok(Map.of("message", "Payment deleted successfully", "id", id));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Payment not found with id: " + id));
    }
}