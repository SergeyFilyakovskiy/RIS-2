package ru.lab.clients.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
    private static SessionFactory factory;

    private HibernateUtil() {}

    public static synchronized SessionFactory getSessionFactory() {
        if (factory == null) {
            String configFile;
            
            // Проверяем, есть ли тестовый конфиг в classpath
            if (HibernateUtil.class.getResource("/hibernate-test.cfg.xml") != null) {
                configFile = "hibernate-test.cfg.xml";
            } else {
                configFile = "hibernate.cfg.xml";
            }
            
            factory = new Configuration().configure(configFile).buildSessionFactory();
        }
        return factory;
    }

    public static synchronized void shutdown() {
        if (factory != null) {
            factory.close();
            factory = null;
        }
    }
}