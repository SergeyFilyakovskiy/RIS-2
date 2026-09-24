package ru.lab.clients;

import ru.lab.clients.generated.ClientType;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TestDataAsserts {

    private TestDataAsserts() {}

    public static void assertSameClient(ClientType expected, ClientType actual) {
        assertAll("сравнение клиента",
                () -> assertEquals(expected.getSurname(), actual.getSurname()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getPatronymic(), actual.getPatronymic()),
                () -> assertEquals(expected.getBirthDate().toXMLFormat(),
                                   actual.getBirthDate().toXMLFormat()),
                () -> assertEquals(expected.getPassportSeries(), actual.getPassportSeries()),
                () -> assertEquals(expected.getPassportNumber(), actual.getPassportNumber()),
                () -> assertEquals(expected.getCity(), actual.getCity()),
                () -> assertEquals(expected.getAddress(), actual.getAddress()),
                () -> assertEquals(expected.getMobilePhone(), actual.getMobilePhone()),
                () -> assertEquals(expected.getEmail(), actual.getEmail()),
                () -> assertEquals(expected.isEmployed(), actual.isEmployed()),  // Boolean
                () -> assertEquals(expected.getPosition(), actual.getPosition()),
                () -> assertEquals(expected.getCitizenship(), actual.getCitizenship()),
                () -> assertEquals(expected.isMilitaryLiable(), actual.isMilitaryLiable()));
    }
}