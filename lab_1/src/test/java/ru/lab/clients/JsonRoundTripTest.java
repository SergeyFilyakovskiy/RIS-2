package ru.lab.clients;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.lab.clients.generated.ClientType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonRoundTripTest extends AbstractDbTest {

    @TempDir
    Path tempDir;

    @Test
    void dbToJsonAndBack() throws Exception {
        Path in = Paths.get(getClass().getResource("/xml/clients.xml").toURI());
        service.importFromXml(in);
        List<ClientType> before = service.readAll();
        assertEquals(3, before.size());

        // СУБД -> JSON
        Path json = tempDir.resolve("clients.json");
        service.exportToJson(json);

        // очищаем СУБД и восстанавливаем из JSON
        service.deleteAll();
        int n = service.importFromJson(json);
        assertEquals(3, n);

        List<ClientType> after = service.readAll();
        assertEquals(before.size(), after.size());
        for (int i = 0; i < before.size(); i++) {
            TestDataAsserts.assertSameClient(before.get(i), after.get(i));
        }
    }
}