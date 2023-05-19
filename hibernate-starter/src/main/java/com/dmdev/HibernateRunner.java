package com.dmdev;

import com.dmdev.converter.BirthDayConverter;
import com.dmdev.entity.Birthday;
import com.dmdev.entity.PersonalInfo;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import com.dmdev.type.JsonType;
import com.dmdev.util.HibernateUtil;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class HibernateRunner {

    private static final Logger log = LoggerFactory.getLogger(HibernateRunner.class);

    public static void main(String[] args) {

        User user = User.builder()
                .username("petr1@gmail.com")
                .personalInfo(PersonalInfo.builder()
                        .firstname("Petr")
                        .lastname("Petrov")
                        .build())
                .build();

        log.info("User entity is in transient state, object: {}", user);

        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) {

            Session session1 = sessionFactory.openSession();
            try (session1) {
                Transaction transaction = session1.beginTransaction();
                log.trace("Transaction is created", transaction);

                session1.saveOrUpdate(user);
                log.trace("User is in persistent state: {}, session {}", user, session1);

                session1.getTransaction().commit();
                log.trace("User is in detached state: {}, session is closed {}", user, session1);
            } catch (Exception exception) {
                log.error("Exception occurred", exception);
                throw exception;
            }
        }
    }
}
