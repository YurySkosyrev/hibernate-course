package com.dmdev;

import com.dmdev.converter.BirthDayConverter;
import com.dmdev.entity.Birthday;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;

public class HibernateRunner {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
//        configuration.addAnnotatedClass(User.class);
//        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        configuration.addAttributeConverter(new BirthDayConverter());
        configuration.configure();

        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            User user = User.builder()
                    .username("ivan35@mail.ru")
                    .firstname("Ivan")
                    .lastname("Ivanov")
                    .birthDate(new Birthday(LocalDate.of(2000, 1, 12)))
                    .role(Role.ADMIN)
                    .build();

            session.save(user);
            session.getTransaction().commit();
        }
    }
}
