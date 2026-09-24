package ru.lab.clients;

import org.junit.jupiter.api.Test;
import ru.lab.clients.generated.ClientType;

import javax.xml.bind.JAXBException;

import static org.junit.jupiter.api.Assertions.*;

class ClientServiceCrudTest extends AbstractDbTest {

    @Test
    void createAndRead() throws Exception {
        ClientType c = client("Иванов", "1990-05-14", "45 19", "123456", "Москва", "Россия");
        c.setMobilePhone("+7 (495) 123-45-67");
        c.setEmail("ivanov@example.com");
        c.setEmployed(true);
        c.setPosition("Инженер");

        long id = service.create(c);
        assertTrue(id > 0);

        ClientType fromDb = service.read(id);
        assertEquals(id, fromDb.getId());
        TestDataAsserts.assertSameClient(c, fromDb);
        assertEquals(1, service.readAll().size());
    }

    @Test
    void update() throws Exception {
        long id = service.create(
                client("Петров", "1991-01-01", "41 20", "111111", "Казань", "Россия"));

        ClientType upd = service.read(id);
        upd.setAddress("Новый адрес, д. 2");
        upd.setMobilePhone("+7 (843) 000-11-22");
        upd.setEmployed(false);
        service.update(upd);

        ClientType reread = service.read(id);
        assertEquals("Новый адрес, д. 2", reread.getAddress());
        assertEquals("+7 (843) 000-11-22", reread.getMobilePhone());
        assertEquals(Boolean.FALSE, reread.isEmployed());  // Boolean
    }

    @Test
    void delete() throws Exception {
        long id = service.create(
                client("Сидоров", "1988-02-02", "50 11", "222222", "Москва", "Беларусь"));
        service.delete(id);
        assertEquals(0, service.readAll().size());
        assertThrows(IllegalArgumentException.class, () -> service.read(id));
    }

    @Test
    void createWithBadMaskRejectedByXsd() {
        ClientType bad = client("Ошибка", "1990-01-01", "AB CD", "123456", "Москва", "Россия");
        assertThrows(JAXBException.class, () -> service.create(bad));
    }

    @Test
    void unknownCityRejected() {
        ClientType bad = client("Город", "1990-01-01", "45 19", "123456", "Атлантида", "Россия");
        assertThrows(IllegalArgumentException.class, () -> service.create(bad));
    }
}