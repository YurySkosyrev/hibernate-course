package com.dmdev;

import com.dmdev.converter.BirthDayConverter;
import com.dmdev.entity.*;
import com.dmdev.type.JsonType;
import com.dmdev.util.HibernateUtil;
import com.dmdev.util.TestDataImporter;
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

        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        Session session = sessionFactory.openSession()) {

//            TestDataImporter.importData(sessionFactory);

            session.beginTransaction();

            User user = session.get(User.class, 1L);
            System.out.println(user.getPayments());
            System.out.println(user.getCompany());

            session.getTransaction().commit();
        }
    }
}
