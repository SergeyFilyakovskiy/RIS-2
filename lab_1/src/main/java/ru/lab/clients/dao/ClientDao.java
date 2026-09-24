package ru.lab.clients.dao;

import org.hibernate.Session;
import ru.lab.clients.entity.Client;

import java.util.List;

public class ClientDao {
    public void persist(Session s, Client c) { s.persist(c); }

    public Client findById(Session s, long id) { return s.get(Client.class, id); }

    public List<Client> findAll(Session s) {
        return s.createQuery("from Client order by id", Client.class).getResultList();
    }

    public void remove(Session s, Client c) { s.remove(c); }

    public void deleteAll(Session s) {
        s.createMutationQuery("delete from Client").executeUpdate();
    }
}