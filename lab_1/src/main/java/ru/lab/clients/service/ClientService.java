package ru.lab.clients.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.lab.clients.config.HibernateUtil;
import ru.lab.clients.dao.CityDao;
import ru.lab.clients.dao.ClientDao;
import ru.lab.clients.dao.CountryDao;
import ru.lab.clients.entity.City;
import ru.lab.clients.entity.Client;
import ru.lab.clients.entity.Country;
import ru.lab.clients.generated.ClientType;
import ru.lab.clients.generated.ClientsType;
import ru.lab.clients.json.JsonService;
import ru.lab.clients.mapper.JaxbMapper;
import ru.lab.clients.xml.XmlService;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

/**
 * API CRUD над клиентами в СУБД + преобразования XML/JSON <-> СУБД.
 * XML/JSON-представление — классы, сгенерированные xjc.
 */
public class ClientService {

    private final ClientDao clientDao = new ClientDao();
    private final CityDao cityDao = new CityDao();
    private final CountryDao countryDao = new CountryDao();
    private final XmlService xml;
    private final JsonService json;

    public ClientService() {
        try {
            this.xml = new XmlService();
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось инициализировать JAXB/XSD", e);
        }
        this.json = new JsonService();
    }

    // ==================== CRUD ====================

    /** CREATE: добавление клиента (объект из XML/JSON) в СУБД. */
    public long create(ClientType j) throws JAXBException {
        xml.validate(j); // контроль масок по XSD
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            Client e = JaxbMapper.toEntity(j,
                    resolveCity(s, j.getCity()),
                    resolveCountry(s, j.getCitizenship()));
            clientDao.persist(s, e);
            tx.commit();
            return e.getId();
        }
    }

    /** READ: чтение одного клиента в XML-представлении. */
    public ClientType read(long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Client e = clientDao.findById(s, id);
            if (e == null) {
                throw new IllegalArgumentException("Клиент с id=" + id + " не найден");
            }
            return JaxbMapper.toJaxb(e);
        }
    }

    /** READ: все клиенты. */
    public List<ClientType> readAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return clientDao.findAll(s).stream().map(JaxbMapper::toJaxb).toList();
        }
    }

    /** UPDATE: обновление по id (id берётся из атрибута объекта). */
    public void update(ClientType j) throws JAXBException {
        if (j.getId() == null) {
            throw new IllegalArgumentException("Для обновления необходим id");
        }
        xml.validate(j);
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            Client e = clientDao.findById(s, j.getId());
            if (e == null) {
                throw new IllegalArgumentException("Клиент с id=" + j.getId() + " не найден");
            }
            JaxbMapper.copyToEntity(j, e,
                    resolveCity(s, j.getCity()),
                    resolveCountry(s, j.getCitizenship()));
            tx.commit();
        }
    }

    /** DELETE. */
    public void delete(long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            Client e = clientDao.findById(s, id);
            if (e == null) {
                throw new IllegalArgumentException("Клиент с id=" + id + " не найден");
            }
            clientDao.remove(s, e);
            tx.commit();
        }
    }

    public void deleteAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            clientDao.deleteAll(s);
            tx.commit();
        }
    }

    // ==================== XML <-> СУБД ====================

    /** Импорт всех клиентов из XML-файла в СУБД (с валидацией по XSD). */
    public int importFromXml(Path file) throws JAXBException {
        ClientsType data = xml.unmarshal(file);
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            int n = 0;
            for (ClientType j : data.getClient()) {
                persistJaxb(s, j);
                n++;
            }
            tx.commit();
            return n;
        }
    }

    /** Обратное преобразование: чтение из СУБД и маршаллинг в XML. */
    public void exportToXml(Path file) throws JAXBException {
        ClientsType data = new ClientsType();
        data.getClient().addAll(readAll());
        xml.marshal(data, file);
    }

    // ==================== JSON <-> СУБД ====================

    /** Импорт клиентов из JSON в СУБД (каждый объект валидируется по XSD). */
    public int importFromJson(Path file) throws JAXBException {
        ClientsType data;
        try {
            data = json.read(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            int n = 0;
            for (ClientType j : data.getClient()) {
                xml.validate(j);
                persistJaxb(s, j);
                n++;
            }
            tx.commit();
            return n;
        }
    }

    /** Обратное преобразование: чтение из СУБД и сериализация в JSON. */
    public void exportToJson(Path file) {
        ClientsType data = new ClientsType();
        data.getClient().addAll(readAll());
        try {
            json.write(data, file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // ==================== Справочники ====================

    public List<String> cityNames() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return cityDao.listAll(s).stream().map(City::getName).toList();
        }
    }

    public List<String> countryNames() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return countryDao.listAll(s).stream().map(Country::getName).toList();
        }
    }

    // ==================== Внутреннее ====================

    private void persistJaxb(Session s, ClientType j) {
        Client e = JaxbMapper.toEntity(j,
                resolveCity(s, j.getCity()),
                resolveCountry(s, j.getCitizenship()));
        clientDao.persist(s, e);
    }

    /** Город — закрытый справочник из 5 городов: неизвестное имя = ошибка. */
    private City resolveCity(Session s, String name) {
        return cityDao.findByName(s, name).orElseThrow(() ->
                new IllegalArgumentException("Город '" + name + "' отсутствует в справочнике. Допустимо: "
                        + cityDao.listAll(s).stream().map(City::getName).toList()));
    }

    /** Гражданство — расширяемый справочник: неизвестную страну добавляем. */
    private Country resolveCountry(Session s, String name) {
        return countryDao.findByName(s, name).orElseGet(() -> {
            Country c = new Country(name);
            s.persist(c);
            return c;
        });
    }
}