package ru.lab.inventory.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.lab.inventory.dto.InventoryItemRequest;
import ru.lab.inventory.dto.InventoryItemResponse;
import ru.lab.inventory.service.InventoryService;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryRestController {

    private final InventoryService service;

    public InventoryRestController(InventoryService service) {
        this.service = service;
    }

    /** GET: все записи. */
    @GetMapping
    public List<InventoryItemResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public InventoryItemResponse getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    /** POST: создание из JSON в теле запроса. */
    @PostMapping
    public ResponseEntity<InventoryItemResponse> create(
            @Valid @RequestBody InventoryItemRequest request,
            UriComponentsBuilder uriBuilder) {
        InventoryItemResponse created = service.create(request);
        URI location = uriBuilder.path("/api/inventory/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}