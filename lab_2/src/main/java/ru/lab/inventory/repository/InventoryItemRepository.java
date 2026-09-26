package ru.lab.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lab.inventory.entity.InventoryItem;
import ru.lab.inventory.entity.ItemStatus;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    boolean existsByInventoryNumber(String inventoryNumber);

    /** Для счётчика «сколько неисправно» в шапке. */
    long countByStatus(ItemStatus status);
}