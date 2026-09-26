package ru.lab.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.lab.inventory.entity.InventoryItem;
import ru.lab.inventory.entity.ItemStatus;
import ru.lab.inventory.repository.InventoryItemRepository;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryRestControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired InventoryItemRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void getAllReturnsJsonList() throws Exception {
        InventoryItem e = new InventoryItem();
        e.setInventoryNumber("INV-1");
        e.setName("Проектор");
        e.setRoom("305");
        e.setQuantity(1);
        e.setStatus(ItemStatus.WORKING);
        repository.save(e);

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].inventoryNumber").value("INV-1"))
                .andExpect(jsonPath("$[0].status").value("WORKING"));
    }

    @Test
    void postValidJsonCreatesItem() throws Exception {
        String json = """
                {"inventoryNumber":"INV-7","name":"Проектор","room":"305",
                 "quantity":1,"status":"WORKING"}
                """;

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.inventoryNumber").value("INV-7"));
        assertEquals(1, repository.count());
    }

    @Test
    void postInvalidJsonReturns400WithFieldErrors() throws Exception {
        String json = """
                {"inventoryNumber":"","name":"P","room":"","quantity":0}
                """;

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("quantity")));
    }

    @Test
    void postDuplicateNumberReturns409() throws Exception {
        InventoryItem e = new InventoryItem();
        e.setInventoryNumber("INV-1");
        e.setName("Проектор");
        e.setRoom("305");
        e.setQuantity(1);
        e.setStatus(ItemStatus.WORKING);
        repository.save(e);

        String json = """
                {"inventoryNumber":"INV-1","name":"Дубликат","room":"310",
                 "quantity":1,"status":"FAULTY"}
                """;

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("INV-1")));
        assertEquals(1, repository.count());
    }

    @Test
    void postMalformedJsonReturns400WithExplanation() throws Exception {
        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON).content("{not a json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Malformed JSON")));
    }

    @Test
    void getMissingItemReturns404WithMessage() throws Exception {
        mockMvc.perform(get("/api/inventory/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("id=999")));
    }

    @Test
    void deleteMissingItemReturns404() throws Exception {
        mockMvc.perform(delete("/api/inventory/999"))
                .andExpect(status().isNotFound());
    }
}