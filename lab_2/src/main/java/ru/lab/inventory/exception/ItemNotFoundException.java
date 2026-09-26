package ru.lab.inventory.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long id) {
        super("Inventory item not found: id=" + id);
    }
}