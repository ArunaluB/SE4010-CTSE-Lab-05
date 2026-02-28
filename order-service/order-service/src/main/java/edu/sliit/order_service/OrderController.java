package edu.sliit.order_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private List<Map<String, Object>> orders = new ArrayList<>();
    private int idCounter = 1;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllOrders() {
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable int id) {
        Optional<Map<String, Object>> order = orders.stream()
                .filter(o -> o.get("id").equals(id))
                .findFirst();

        if (order.isPresent()) {
            return ResponseEntity.ok(order.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Order not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> placeOrder(@RequestBody Map<String, Object> order) {
        order.put("id", idCounter++);
        order.put("status", "PENDING");
        order.put("orderDate", LocalDateTime.now().toString());
        orders.add(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable int id, @RequestBody Map<String, Object> updatedOrder) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).get("id").equals(id)) {
                updatedOrder.put("id", id);
                orders.set(i, updatedOrder);
                return ResponseEntity.ok(updatedOrder);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Order not found with id: " + id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable int id, @RequestBody Map<String, String> statusUpdate) {
        for (Map<String, Object> order : orders) {
            if (order.get("id").equals(id)) {
                order.put("status", statusUpdate.get("status"));
                return ResponseEntity.ok(order);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Order not found with id: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable int id) {
        boolean removed = orders.removeIf(o -> o.get("id").equals(id));
        if (removed) {
            return ResponseEntity.ok(Map.of("message", "Order deleted successfully", "id", id));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Order not found with id: " + id));
    }
}
