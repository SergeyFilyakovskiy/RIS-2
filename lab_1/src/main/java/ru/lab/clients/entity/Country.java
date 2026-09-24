
package ru.lab.clients.entity;

import jakarta.persistence.*;
import java.util.Objects;

/** Справочник стран (гражданство — элемент типа "Список"). */
@Entity
@Table(name = "countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 64)
    private String name;

    public Country() {}
    public Country(String name) { this.name = name; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Country c && id != null && id.equals(c.id);
    }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
    @Override
    public String toString() { return name; }
}