package ru.lab.inventory.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab.inventory.dto.InventoryItemRequest;
import ru.lab.inventory.dto.InventoryItemResponse;
import ru.lab.inventory.entity.InventoryItem;
import ru.lab.inventory.entity.ItemStatus;
import ru.lab.inventory.exception.DuplicateInventoryNumberException;
import ru.lab.inventory.exception.ItemNotFoundException;
import ru.lab.inventory.repository.InventoryItemRepository;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryItemRepository repository;

    public InventoryService(InventoryItemRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> findAll() {
        return repository.findAll(Sort.by("inventoryNumber"))
                .stream().map(InventoryItemResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public long countFaulty() {
        return repository.countByStatus(ItemStatus.FAULTY);
    }

    @Transactional(readOnly = true)
    public InventoryItemResponse findById(Long id) {
        return InventoryItemResponse.from(
                repository.findById(id).orElseThrow(() -> new ItemNotFoundException(id)));
    }

    @Transactional
    public InventoryItemResponse create(InventoryItemRequest request) {
        String number = request.getInventoryNumber().trim();
        if (repository.existsByInventoryNumber(number)) {
            throw new DuplicateInventoryNumberException(number);
        }
        InventoryItem entity = new InventoryItem();
        apply(entity, request);
        entity.setInventoryNumber(number);
        return InventoryItemResponse.from(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        InventoryItem entity = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        repository.delete(entity);
    }

    private void apply(InventoryItem entity, InventoryItemRequest r) {
        entity.setInventoryNumber(r.getInventoryNumber().trim());
        entity.setName(r.getName().trim());
        entity.setRoom(r.getRoom().trim());
        entity.setQuantity(r.getQuantity());
        entity.setStatus(r.getStatus());
    }
}