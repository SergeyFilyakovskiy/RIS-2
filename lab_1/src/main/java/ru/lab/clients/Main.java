package ru.lab.clients;

import ru.lab.clients.config.HibernateUtil;
import ru.lab.clients.generated.ClientType;
import ru.lab.clients.init.ReferenceInitializer;
import ru.lab.clients.mapper.JaxbMapper;
import ru.lab.clients.service.ClientService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public final class Main {

    private static final ClientService service = new ClientService();
    private static final Scanner sc = new Scanner(System.in);

    private static final Path DEF_XML_IN   = Paths.get("src/main/resources/xml/clients.xml");
    private static final Path DEF_XML_OUT  = Paths.get("clients-export.xml");
    private static final Path DEF_JSON_OUT = Paths.get("clients-export.json");

    public static void main(String[] args) {
        ReferenceInitializer.initReferences();
        boolean work = true;
        while (work) {
            printMenu();
            String cmd = sc.nextLine().trim();
            try {
                switch (cmd) {
                    case "1" -> { int n = service.importFromXml(path("XML-файл для импорта", DEF_XML_IN));
                                  System.out.println("Импортировано клиентов: " + n); }
                    case "2" -> { service.exportToXml(path("XML-файл для экспорта", DEF_XML_OUT));
                                  System.out.println("Экспорт в XML выполнен"); }
                    case "3" -> { int n = service.importFromJson(path("JSON-файл для импорта", DEF_JSON_OUT));
                                  System.out.println("Импортировано клиентов из JSON: " + n); }
                    case "4" -> { service.exportToJson(path("JSON-файл для экспорта", DEF_JSON_OUT));
                                  System.out.println("Экспорт в JSON выполнен"); }
                    case "5" -> service.readAll().forEach(Main::printCard);
                    case "6" -> printCard(service.read(Long.parseLong(ask("ID клиента"))));
                    case "7" -> addClient();
                    case "8" -> updateClient();
                    case "9" -> { service.delete(Long.parseLong(ask("ID клиента")));
                                  System.out.println("Удалено"); }
                    case "0" -> work = false;
                    default  -> System.out.println("Неизвестная команда");
                }
            } catch (Exception e) {
                System.out.println("ОШИБКА: " + e.getMessage());
            }
        }
        HibernateUtil.shutdown();
    }

    private static void printMenu() {
        System.out.println("""
                =============== МЕНЮ (вариант 27) ===============
                1 - Импорт клиентов из XML в СУБД (JAXB, валидация XSD)
                2 - Экспорт из СУБД в XML (обратное преобразование)
                3 - Импорт клиентов из JSON в СУБД (gson)
                4 - Экспорт из СУБД в JSON (обратное преобразование)
                5 - Показать всех клиентов (READ)
                6 - Клиент по ID (READ)
                7 - Добавить клиента (CREATE, ручной ввод)
                8 - Обновить клиента (UPDATE)
                9 - Удалить клиента (DELETE)
                0 - Выход""");
    }

    private static void addClient() throws Exception {
        ClientType c = new ClientType();
        c.setSurname(ask("Фамилия"));
        c.setName(ask("Имя"));
        c.setPatronymic(ask("Отчество"));
        c.setBirthDate(JaxbMapper.toXmlDate(LocalDate.parse(ask("Дата рождения (ГГГГ-ММ-ДД)"))));
        c.setPassportSeries(ask("Серия паспорта (ДД ДД)"));
        c.setPassportNumber(ask("Номер паспорта (ДДДДДД)"));
        c.setCity(pickFromReference("Город проживания", service.cityNames()));
        c.setAddress(ask("Адрес факт. проживания"));
        c.setMobilePhone(opt(ask("Моб. телефон (+7 (XXX) XXX-XX-XX), пусто - нет")));
        c.setEmail(opt(ask("E-mail, пусто - нет")));
        String emp = ask("Трудоустроен (true/false), пусто - null");
        c.setEmployed(emp.isEmpty() ? null : Boolean.parseBoolean(emp));  // Boolean
        c.setPosition(opt(ask("Должность, пусто - нет")));
        System.out.println("Справочник стран: " + service.countryNames() + " (можно ввести новую)");
        c.setCitizenship(ask("Гражданство"));
        c.setMilitaryLiable(Boolean.parseBoolean(ask("Военнообязанный (true/false)")));
        System.out.println("Создан клиент с id=" + service.create(c));
    }

    private static void updateClient() throws Exception {
        ClientType c = service.read(Long.parseLong(ask("ID клиента")));
        printCard(c);
        replace(ask("Фамилия [" + c.getSurname() + "]"), c::setSurname);
        replace(ask("Имя [" + c.getName() + "]"), c::setName);
        replace(ask("Отчество [" + c.getPatronymic() + "]"), c::setPatronymic);
        replace(ask("Адрес [" + c.getAddress() + "]"), c::setAddress);
        replace(ask("Моб. телефон [" + c.getMobilePhone() + "]"), c::setMobilePhone);
        replace(ask("E-mail [" + c.getEmail() + "]"), c::setEmail);
        replace(ask("Должность [" + c.getPosition() + "]"), c::setPosition);
        String emp = ask("Трудоустроен (true/false) [" + c.isEmployed() + "]");  // Boolean
        if (!emp.isEmpty()) c.setEmployed(Boolean.parseBoolean(emp));
        String mil = ask("Военнообязанный (true/false) [" + c.isMilitaryLiable() + "]");
        if (!mil.isEmpty()) c.setMilitaryLiable(Boolean.parseBoolean(mil));
        String city = ask("Город (имя) [" + c.getCity() + "]");
        if (!city.isEmpty()) c.setCity(city);
        service.update(c);
        System.out.println("Обновлено");
    }

    private static void replace(String input, java.util.function.Consumer<String> setter) {
        if (!input.isEmpty()) setter.accept(input);
    }

    private static String pickFromReference(String title, List<String> items) {
        System.out.println(title + ":");
        for (int i = 0; i < items.size(); i++) System.out.println("  " + (i + 1) + ") " + items.get(i));
        int idx = Integer.parseInt(ask("Номер")) - 1;
        return items.get(idx);
    }

    private static void printCard(ClientType c) {
        System.out.printf("""
                --- Клиент id=%s ---
                ФИО: %s %s %s, дата рождения: %s
                Паспорт: %s %s
                Город: %s; адрес: %s
                Телефон: %s; e-mail: %s
                Трудоустроен: %s%s
                Гражданство: %s; военнообязанный: %s%n""",
                c.getId(), c.getSurname(), c.getName(), c.getPatronymic(), c.getBirthDate(),
                c.getPassportSeries(), c.getPassportNumber(),
                c.getCity(), c.getAddress(),
                c.getMobilePhone(), c.getEmail(),
                c.isEmployed(),  // Boolean
                c.getPosition() == null ? "" : " (" + c.getPosition() + ")",
                c.getCitizenship(), c.isMilitaryLiable());
    }

    private static Path path(String title, Path def) {
        String v = ask(title + " [" + def + "]");
        return v.isEmpty() ? def : Paths.get(v);
    }

    private static String opt(String v) { return v.isEmpty() ? null : v; }

    private static String ask(String prompt) {
        System.out.print(prompt + ": ");
        return sc.nextLine().trim();
    }
}