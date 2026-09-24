package ru.lab.clients.init;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.lab.clients.config.HibernateUtil;
import ru.lab.clients.entity.City;
import ru.lab.clients.entity.Country;

import java.util.List;

/** Заполнение справочников (элементы типа "Список"). */
public final class ReferenceInitializer {

    /** Ровно 5 городов по условию ("Список (от 5 городов)"). */
    public static final List<String> CITIES = List.of(
            "Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург", "Казань");

    public static final List<String> COUNTRIES = List.of(
            "Россия", "Беларусь", "Казахстан", "Армения", "Кыргызстан", "Узбекистан");

    private ReferenceInitializer() {}

    public static void initReferences() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            if (count(s, City.class) == 0) {
                CITIES.forEach(n -> s.persist(new City(n)));
            }
            if (count(s, Country.class) == 0) {
                COUNTRIES.forEach(n -> s.persist(new Country(n)));
            }
            tx.commit();
        }
    }

    private static long count(Session s, Class<?> type) {
        return s.createQuery("select count(t) from " + type.getSimpleName() + " t", Long.class)
                .getSingleResult();
    }
}