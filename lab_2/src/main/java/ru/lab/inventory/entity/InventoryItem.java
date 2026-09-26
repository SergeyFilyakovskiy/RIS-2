package ru.lab.inventory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory_items",
       uniqueConstraints = @UniqueConstraint(name = "uk_inventory_number",
                                             columnNames = "inventory_number"))
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Инвентарный номер — уникален (правило варианта). */
    @Column(name = "inventory_number", nullable = false, unique = true, length = 32)
    private String inventoryNumber;

    /** Название. */
    @Column(nullable = false, length = 128)
    private String name;

    /** Кабинет. */
    @Column(nullable = false, length = 64)
    private String room;

    /** Количество. */
    @Column(nullable = false)
    private Integer quantity;

    /** Статус: исправен / неисправен. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ItemStatus status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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