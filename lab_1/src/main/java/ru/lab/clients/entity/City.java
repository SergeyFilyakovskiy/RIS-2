package ru.lab.clients.entity;

import jakarta.persistence.*;
import java.util.Objects;

/** Справочник городов (элемент типа "Список" из варианта). */
@Entity
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 64)
    private String name;

    public City() {}
    public City(String name) { this.name = name; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        return o instanceof City c && id != null && id.equals(c.id);
    }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
    @Override
    public String toString() { return name; }
}