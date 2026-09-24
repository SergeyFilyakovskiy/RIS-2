package ru.lab.clients.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.lab.clients.generated.ClientsType;
import ru.lab.clients.mapper.JaxbMapper;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/** Преобразование клиентов в JSON и обратно (gson). */
public class JsonService {

    private final Gson gson;

    public JsonService() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(XMLGregorianCalendar.class, new XmlGregorianCalendarAdapter())
                .create();
    }

    public ClientsType read(Path path) throws IOException {
        try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return gson.fromJson(r, ClientsType.class);
        }
    }

    public void write(ClientsType data, Path path) throws IOException {
        Files.writeString(path, gson.toJson(data), StandardCharsets.UTF_8);
    }

    /** Дата в JSON хранится как ISO-строка "1990-05-14". */
    private static class XmlGregorianCalendarAdapter
            implements JsonSerializer<XMLGregorianCalendar>, JsonDeserializer<XMLGregorianCalendar> {
        @Override
        public JsonElement serialize(XMLGregorianCalendar src, Type t,
                                     JsonSerializationContext ctx) {
            return new JsonPrimitive(src.toXMLFormat());
        }

        @Override
        public XMLGregorianCalendar deserialize(JsonElement json, Type t,
                                                JsonDeserializationContext ctx) throws JsonParseException {
            return JaxbMapper.toXmlDate(LocalDate.parse(json.getAsString()));
        }
    }
}