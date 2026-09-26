package ru.lab.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.lab.inventory.entity.InventoryItem;
import ru.lab.inventory.entity.ItemStatus;
import ru.lab.inventory.repository.InventoryItemRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InventoryItemRepositoryTest {

    @Autowired
    InventoryItemRepository repository;

    private InventoryItem item(String number, ItemStatus st) {
        InventoryItem e = new InventoryItem();
        e.setInventoryNumber(number);
        e.setName("Тест");
        e.setRoom("305");
        e.setQuantity(1);
        e.setStatus(st);
        return e;
    }

    @Test
    void countByStatusCountsFaultyOnly() {
        repository.save(item("INV-1", ItemStatus.WORKING));
        repository.save(item("INV-2", ItemStatus.FAULTY));
        repository.save(item("INV-3", ItemStatus.FAULTY));

        assertEquals(2, repository.countByStatus(ItemStatus.FAULTY));
        assertTrue(repository.existsByInventoryNumber("INV-1"));
        assertFalse(repository.existsByInventoryNumber("INV-9"));
    }

    @Test
    void uniqueInventoryNumberEnforcedByDb() {
        repository.saveAndFlush(item("INV-1", ItemStatus.WORKING));
        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(item("INV-1", ItemStatus.FAULTY)));
    }
}