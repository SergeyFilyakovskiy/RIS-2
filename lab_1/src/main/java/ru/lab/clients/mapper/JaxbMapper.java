package ru.lab.clients.mapper;

import ru.lab.clients.entity.City;
import ru.lab.clients.entity.Client;
import ru.lab.clients.entity.Country;
import ru.lab.clients.generated.ClientType;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

public final class JaxbMapper {

    private JaxbMapper() {}

    public static Client toEntity(ClientType j, City city, Country country) {
        Client e = new Client();
        copyToEntity(j, e, city, country);
        return e;
    }

    public static void copyToEntity(ClientType j, Client e, City city, Country country) {
        e.setSurname(j.getSurname());
        e.setName(j.getName());
        e.setPatronymic(j.getPatronymic());
        e.setBirthDate(toLocalDate(j.getBirthDate()));
        e.setPassportSeries(j.getPassportSeries());
        e.setPassportNumber(j.getPassportNumber());
        e.setCity(city);
        e.setAddress(j.getAddress());
        e.setMobilePhone(j.getMobilePhone());
        e.setEmail(j.getEmail());
        e.setEmployed(j.isEmployed());  // Boolean -> Boolean
        e.setPosition(j.getPosition());
        e.setCitizenship(country);
        e.setMilitaryLiable(j.isMilitaryLiable());
    }

    public static ClientType toJaxb(Client e) {
        ClientType j = new ClientType();
        j.setId(e.getId());
        j.setSurname(e.getSurname());
        j.setName(e.getName());
        j.setPatronymic(e.getPatronymic());
        j.setBirthDate(toXmlDate(e.getBirthDate()));
        j.setPassportSeries(e.getPassportSeries());
        j.setPassportNumber(e.getPassportNumber());
        j.setCity(e.getCity().getName());
        j.setAddress(e.getAddress());
        j.setMobilePhone(e.getMobilePhone());
        j.setEmail(e.getEmail());
        j.setEmployed(e.getEmployed());  // Boolean -> Boolean
        j.setPosition(e.getPosition());
        j.setCitizenship(e.getCitizenship().getName());
        j.setMilitaryLiable(e.getMilitaryLiable());
        return j;
    }

    public static XMLGregorianCalendar toXmlDate(LocalDate d) {
        return DatatypeFactory.newDefaultInstance().newXMLGregorianCalendarDate(
                d.getYear(), d.getMonthValue(), d.getDayOfMonth(),
                DatatypeConstants.FIELD_UNDEFINED);
    }

    public static LocalDate toLocalDate(XMLGregorianCalendar x) {
        return LocalDate.of(x.getYear(), x.getMonth(), x.getDay());
    }
}