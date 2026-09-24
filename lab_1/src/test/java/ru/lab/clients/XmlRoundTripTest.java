package ru.lab.clients;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.lab.clients.generated.ClientsType;
import ru.lab.clients.xml.XmlService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XmlRoundTripTest extends AbstractDbTest {

    @TempDir
    Path tempDir;

    @Test
    void importFromXmlThenExportBack() throws Exception {
        XmlService xml = new XmlService();
        Path in = Paths.get(getClass().getResource("/xml/clients.xml").toURI());

        // XML -> СУБД
        int n = service.importFromXml(in);
        assertEquals(3, n);
        List<?> fromDb = service.readAll();
        assertEquals(3, fromDb.size());

        // СУБД -> XML (обратное преобразование)
        Path out = tempDir.resolve("export.xml");
        service.exportToXml(out);

        ClientsType exported = xml.unmarshal(out);
        ClientsType source = xml.unmarshal(in);
        assertEquals(source.getClient().size(), exported.getClient().size());
        for (int i = 0; i < source.getClient().size(); i++) {
            TestDataAsserts.assertSameClient(source.getClient().get(i), exported.getClient().get(i));
            assertNotNull(exported.getClient().get(i).getId(), "в экспорте должен быть id из СУБД");
        }
    }
}