package edu.sliit.item_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/items")
public class ItemController {

    private List<Map<String, Object>> items = new ArrayList<>();
    private int idCounter = 1;

    public ItemController() {
        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", idCounter++);
        item1.put("name", "Book");
        item1.put("price", 29.99);
        items.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", idCounter++);
        item2.put("name", "Laptop");
        item2.put("price", 1299.99);
        items.add(item2);

        Map<String, Object> item3 = new HashMap<>();
        item3.put("id", idCounter++);
        item3.put("name", "Phone");
        item3.put("price", 899.99);
        items.add(item3);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllItems() {
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable int id) {
        Optional<Map<String, Object>> item = items.stream()
                .filter(i -> i.get("id").equals(id))
                .findFirst();

        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Item not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addItem(@RequestBody Map<String, Object> item) {
        item.put("id", idCounter++);
        items.add(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable int id, @RequestBody Map<String, Object> updatedItem) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).get("id").equals(id)) {
                updatedItem.put("id", id);
                items.set(i, updatedItem);
                return ResponseEntity.ok(updatedItem);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Item not found with id: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable int id) {
        boolean removed = items.removeIf(i -> i.get("id").equals(id));
        if (removed) {
            return ResponseEntity.ok(Map.of("message", "Item deleted successfully", "id", id));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Item not found with id: " + id));
    }
}