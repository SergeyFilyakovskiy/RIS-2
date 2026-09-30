package ru.lab.clients.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/** Клиент, состав полей — вариант 27. */
@Entity
@Table(name = "clients",
       uniqueConstraints = @UniqueConstraint(columnNames = {"passport_series", "passport_number"}))
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128) private String surname;
    @Column(nullable = false, length = 128) private String name;
    @Column(nullable = false, length = 128) private String patronymic;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "passport_series", nullable = false, length = 5)  private String passportSeries;
    @Column(name = "passport_number", nullable = false, length = 6)  private String passportNumber;

    /** Список: город проживания -> справочник cities. */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id")
    private City city;

    @Column(nullable = false, length = 255) private String address;

    @Column(name = "mobile_phone", length = 20) private String mobilePhone;
    @Column(length = 128)                       private String email;
    @Column                                     private Boolean employed;
    @Column(length = 128)                       private String position;

    /** Список: гражданство -> справочник countries. */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "citizenship_id")
    private Country citizenship;

    @Column(name = "military_liable", nullable = false)
    private Boolean militaryLiable;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPatronymic() { return patronymic; }
    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getPassportSeries() { return passportSeries; }
    public void setPassportSeries(String s) { this.passportSeries = s; }
    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String n) { this.passportNumber = n; }
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String p) { this.mobilePhone = p; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Boolean getEmployed() { return employed; }
    public void setEmployed(Boolean employed) { this.employed = employed; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public Country getCitizenship() { return citizenship; }
    public void setCitizenship(Country c) { this.citizenship = c; }
    public Boolean getMilitaryLiable() { return militaryLiable; }
    public void setMilitaryLiable(Boolean m) { this.militaryLiable = m; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Client c && id != null && id.equals(c.id);
    }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}