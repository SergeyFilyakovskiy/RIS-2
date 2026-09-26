package ru.lab.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.lab.inventory.entity.InventoryItem;
import ru.lab.inventory.entity.ItemStatus;
import ru.lab.inventory.repository.InventoryItemRepository;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryWebControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired InventoryItemRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    private InventoryItem item(String number, String name, String room, int qty, ItemStatus st) {
        InventoryItem e = new InventoryItem();
        e.setInventoryNumber(number);
        e.setName(name);
        e.setRoom(room);
        e.setQuantity(qty);
        e.setStatus(st);
        return e;
    }

    @Test
    void listShowsItemsAndFaultyCounter() throws Exception {
        repository.save(item("INV-1", "Проектор", "305", 1, ItemStatus.WORKING));
        repository.save(item("INV-2", "Стул", "305", 2, ItemStatus.FAULTY));

        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Проектор")))
                .andExpect(model().attribute("faultyCount", 1L));
    }

    @Test
    void addValidItemRedirectsAndPersists() throws Exception {
        mockMvc.perform(post("/inventory")
                        .param("inventoryNumber", "INV-10")
                        .param("name", "Микроскоп")
                        .param("room", "310")
                        .param("quantity", "3")
                        .param("status", "WORKING"))
                .andExpect(status().is3xxRedirection());
        assertEquals(1, repository.count());
    }

    @Test
    void addInvalidItemShowsFieldErrors() throws Exception {
        mockMvc.perform(post("/inventory")
                        .param("inventoryNumber", "")
                        .param("name", "X")
                        .param("room", "")
                        .param("quantity", "0")
                        .param("status", "WORKING"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form",
                        "inventoryNumber", "name", "room", "quantity"));
        assertEquals(0, repository.count());
    }

    @Test
    void duplicateNumberRejectedWithFieldError() throws Exception {
        repository.save(item("INV-1", "Проектор", "305", 1, ItemStatus.WORKING));

        mockMvc.perform(post("/inventory")
                        .param("inventoryNumber", "INV-1")
                        .param("name", "Дубликат")
                        .param("room", "310")
                        .param("quantity", "1")
                        .param("status", "WORKING"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "inventoryNumber"));
        assertEquals(1, repository.count());
    }

    @Test
    void deleteRemovesItem() throws Exception {
        InventoryItem saved = repository.save(item("INV-5", "Парта", "301", 1, ItemStatus.WORKING));

        mockMvc.perform(post("/inventory/" + saved.getId() + "/delete"))
                .andExpect(status().is3xxRedirection());
        assertEquals(0, repository.count());
    }

    @Test
    void i18nSwitchesToEnglish() throws Exception {
        mockMvc.perform(get("/inventory").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Classroom inventory")))
                .andExpect(content().string(containsString("Faulty:")));
    }
}