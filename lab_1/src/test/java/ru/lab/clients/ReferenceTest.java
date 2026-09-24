package ru.lab.clients;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReferenceTest extends AbstractDbTest {

    @Test
    void cityReferenceContainsExactlyFiveCities() {
        var cities = service.cityNames();
        assertEquals(5, cities.size());
        assertTrue(cities.containsAll(java.util.List.of(
                "Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург", "Казань")));
    }

    @Test
    void countryReferenceSeeded() {
        assertTrue(service.countryNames().contains("Россия"));
    }
}