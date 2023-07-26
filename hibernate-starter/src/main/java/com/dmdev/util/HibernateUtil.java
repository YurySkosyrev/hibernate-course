package com.dmdev.util;

import com.dmdev.converter.BirthDayConverter;
import com.dmdev.entity.Audit;
import com.dmdev.entity.User;
import com.dmdev.interceptor.GlobalInterceptor;
import com.dmdev.listener.AuditTableListener;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.Service;

@UtilityClass
public class HibernateUtil {

    public static SessionFactory buildSessionFactory() {
        Configuration configuration = buildConfiguration();
        configuration.configure();

        SessionFactory sessionFactory = configuration.buildSessionFactory();
        registerListeners(sessionFactory);

        return sessionFactory;
    }

    private static void registerListeners(SessionFactory sessionFactory) {
        // у SessionFactory нет функционала для добавления EventListener, поэтому мы должны его
        // явно привести к его реализации SessionFactoryImpl с помощью метода unwrap
        // метод unwrap делает явное приведение типов (type cast)
        SessionFactoryImpl sessionFactoryImpl = sessionFactory.unwrap(SessionFactoryImpl.class);

        // вызываем getServiceRegistry
        // ServiceRegistry - объект, которые занимается регистрацией различных сервисов
        // сервис реализует интерфейс Service. Service в Hibernate может быть всё, что угодно
        // диалекты, listeners, java transaction API и т.д.
        EventListenerRegistry listenerRegistry = sessionFactoryImpl.getServiceRegistry().getService(EventListenerRegistry.class);
        AuditTableListener auditTableListener = new AuditTableListener();

        // добавляем Listeners в группы
        // можно добавлять в конец или начала или вообще переопределить
        // тип EventType должен совпадать с типом EventListener
        listenerRegistry.appendListeners(EventType.PRE_INSERT, auditTableListener);
        listenerRegistry.appendListeners(EventType.PRE_DELETE, auditTableListener);
    }

    public static Configuration buildConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Audit.class);
        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        configuration.addAttributeConverter(new BirthDayConverter());
        configuration.registerTypeOverride(new JsonBinaryType());
        configuration.setInterceptor(new GlobalInterceptor());
        configuration.configure();
        return configuration;
    }
}
