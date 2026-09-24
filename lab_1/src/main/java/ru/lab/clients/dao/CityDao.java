package ru.lab.clients.dao;

import org.hibernate.Session;
import ru.lab.clients.entity.City;

import java.util.List;
import java.util.Optional;

public class CityDao {
    public Optional<City> findByName(Session s, String name) {
        return s.createQuery("from City where name = :n", City.class)
                .setParameter("n", name)
                .uniqueResultOptional();
    }

    public List<City> listAll(Session s) {
        return s.createQuery("from City order by id", City.class).getResultList();
    }
}