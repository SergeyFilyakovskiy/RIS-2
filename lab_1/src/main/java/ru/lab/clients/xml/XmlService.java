package ru.lab.clients.xml;

import org.xml.sax.SAXException;
import ru.lab.clients.generated.ClientType;
import ru.lab.clients.generated.ClientsType;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;

/** Маршаллинг/анмаршаллинг XML с валидацией по XSD (маски полей!). */
public class XmlService {

    private final JAXBContext context;
    private final Schema schema;

    public XmlService() throws JAXBException, SAXException {
        this.context = JAXBContext.newInstance(ClientsType.class, ClientType.class);
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        this.schema = sf.newSchema(XmlService.class.getResource("/schema/clients.xsd"));
    }

    /** Чтение файла с валидацией по схеме. */
    public ClientsType unmarshal(Path path) throws JAXBException {
        Unmarshaller u = context.createUnmarshaller();
        u.setSchema(schema);
        return (ClientsType) u.unmarshal(path.toFile());
    }

    /** Запись в файл. */
    public void marshal(ClientsType data, Path path) throws JAXBException {
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(data, path.toFile());
    }

    /** Проверка одного объекта по XSD (используется при ручном вводе и импорте JSON). */
    public void validate(ClientType c) throws JAXBException {
        ClientsType wrap = new ClientsType();
        wrap.getClient().add(c);
        StringWriter w = new StringWriter();
        context.createMarshaller().marshal(wrap, w);

        Unmarshaller u = context.createUnmarshaller();
        u.setSchema(schema);
        u.unmarshal(new StreamSource(new StringReader(w.toString())), ClientsType.class);
    }
}