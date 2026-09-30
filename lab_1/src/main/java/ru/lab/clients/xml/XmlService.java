package ru.lab.clients.xml;

import org.xml.sax.SAXException;
import ru.lab.clients.generated.ClientType;
import ru.lab.clients.generated.ClientsType;
import ru.lab.clients.generated.ObjectFactory;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;

public class XmlService {

    private static final String NS = "http://clients.lab.var27/xml";
    private static final QName CLIENTS_QNAME = new QName(NS, "clients");

    private final JAXBContext context;
    private final Schema schema;

    public XmlService() throws JAXBException, SAXException {
        // Включаем ObjectFactory — именно он содержит @XmlElementDecl для корневого <clients>
        this.context = JAXBContext.newInstance(ObjectFactory.class);
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        this.schema = sf.newSchema(XmlService.class.getResource("/schema/clients.xsd"));
    }

    /** Чтение файла с валидацией по XSD. */
    public ClientsType unmarshal(Path path) throws JAXBException {
        Unmarshaller u = context.createUnmarshaller();
        u.setSchema(schema);
        // unmarshal возвращает JAXBElement<ClientsType>, потому что корневой элемент описан через ObjectFactory
        Object result = u.unmarshal(path.toFile());
        if (result instanceof JAXBElement) {
            @SuppressWarnings("unchecked")
            JAXBElement<ClientsType> je = (JAXBElement<ClientsType>) result;
            return je.getValue();
        }
        return (ClientsType) result;
    }

    /** Запись в файл (с правильной обёрткой в JAXBElement). */
    public void marshal(ClientsType data, Path path) throws JAXBException {
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<ClientsType> root = new JAXBElement<>(CLIENTS_QNAME, ClientsType.class, data);
        m.marshal(root, path.toFile());
    }

    /** Валидация одного ClientType через XSD (сериализуем → читаем обратно со схемой). */
    public void validate(ClientType c) throws JAXBException {
        ClientsType wrap = new ClientsType();
        wrap.getClient().add(c);
        JAXBElement<ClientsType> root = new JAXBElement<>(CLIENTS_QNAME, ClientsType.class, wrap);

        StringWriter w = new StringWriter();
        context.createMarshaller().marshal(root, w);

        Unmarshaller u = context.createUnmarshaller();
        u.setSchema(schema);
        u.unmarshal(new StreamSource(new StringReader(w.toString())));
    }
}