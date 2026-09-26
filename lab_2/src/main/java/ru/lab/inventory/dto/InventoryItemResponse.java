package ru.lab.inventory.dto;

import ru.lab.inventory.entity.InventoryItem;
import ru.lab.inventory.entity.ItemStatus;

public record InventoryItemResponse(
        Long id,
        String inventoryNumber,
        String name,
        String room,
        Integer quantity,
        ItemStatus status) {

    public static InventoryItemResponse from(InventoryItem e) {
        return new InventoryItemResponse(
                e.getId(), e.getInventoryNumber(), e.getName(),
                e.getRoom(), e.getQuantity(), e.getStatus());
    }
}