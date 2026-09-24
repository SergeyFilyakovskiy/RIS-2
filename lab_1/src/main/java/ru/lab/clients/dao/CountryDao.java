package ru.lab.clients.dao;

import org.hibernate.Session;
import ru.lab.clients.entity.Country;

import java.util.List;
import java.util.Optional;

public class CountryDao {
    public Optional<Country> findByName(Session s, String name) {
        return s.createQuery("from Country where name = :n", Country.class)
                .setParameter("n", name)
                .uniqueResultOptional();
    }

    public List<Country> listAll(Session s) {
        return s.createQuery("from Country order by id", Country.class).getResultList();
    }
}