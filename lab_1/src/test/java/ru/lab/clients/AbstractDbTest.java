package ru.lab.clients;

import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.lab.clients.config.HibernateUtil;
import ru.lab.clients.generated.ClientType;
import ru.lab.clients.init.ReferenceInitializer;
import ru.lab.clients.mapper.JaxbMapper;
import ru.lab.clients.service.ClientService;

import java.time.LocalDate;

public abstract class AbstractDbTest {

    protected static ClientService service;

    @BeforeAll
    static void setUpAll() {
        ReferenceInitializer.initReferences();
        service = new ClientService();
    }

    @AfterAll
    static void tearDownAll() {
        HibernateUtil.shutdown();
    }

    @BeforeEach
    void cleanClients() {
        service.deleteAll();
    }

    protected static ClientType client(String surname, String date, String series,
                                       String number, String city, String citizenship) {
        ClientType c = new ClientType();
        c.setSurname(surname);
        c.setName("Имя");
        c.setPatronymic("Отчество");
        c.setBirthDate(JaxbMapper.toXmlDate(LocalDate.parse(date)));
        c.setPassportSeries(series);
        c.setPassportNumber(number);
        c.setCity(city);
        c.setAddress("ул. Тестовая, д. 1, кв. 1");
        c.setCitizenship(citizenship);
        c.setMilitaryLiable(true);
        return c;
    }
}