package ru.lab.inventory.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.lab.inventory.entity.ItemStatus;

public class InventoryItemRequest {

    @NotBlank(message = "{validation.number.blank}")
    @Size(max = 32, message = "{validation.number.size}")
    @Pattern(regexp = "[A-Za-z0-9\\-]+", message = "{validation.number.pattern}")
    private String inventoryNumber;

    @NotBlank(message = "{validation.name.blank}")
    @Size(min = 2, max = 128, message = "{validation.name.size}")
    private String name;

    @NotBlank(message = "{validation.room.blank}")
    @Size(max = 64, message = "{validation.room.size}")
    private String room;

    @NotNull(message = "{validation.quantity.null}")
    @Min(value = 1, message = "{validation.quantity.min}")
    @Max(value = 10000, message = "{validation.quantity.max}")
    private Integer quantity;

    @NotNull(message = "{validation.status.null}")
    private ItemStatus status;

    public String getInventoryNumber() { return inventoryNumber; }
    public void setInventoryNumber(String v) { this.inventoryNumber = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getRoom() { return room; }
    public void setRoom(String v) { this.room = v; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer v) { this.quantity = v; }
    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus v) { this.status = v; }
}