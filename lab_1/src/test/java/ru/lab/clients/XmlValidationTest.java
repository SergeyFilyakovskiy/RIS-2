package ru.lab.clients;

import org.junit.jupiter.api.Test;
import ru.lab.clients.xml.XmlService;

import javax.xml.bind.JAXBException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;

class XmlValidationTest {

    @Test
    void badPassportSeriesFailsXsdValidation() throws Exception {
        XmlService xml = new XmlService();
        assertThrows(JAXBException.class, () -> xml.unmarshal(
                Paths.get(XmlValidationTest.class.getResource("/xml/bad-passport.xml").toURI())));
    }
}