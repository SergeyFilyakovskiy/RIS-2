package ru.lab.inventory.exception;

public class DuplicateInventoryNumberException extends RuntimeException {
    public DuplicateInventoryNumberException(String inventoryNumber) {
        super("Inventory number already exists: " + inventoryNumber);
    }
}