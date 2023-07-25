package com.dmdev;

import com.dmdev.converter.BirthDayConverter;
import com.dmdev.entity.*;
import com.dmdev.type.JsonType;
import com.dmdev.util.HibernateUtil;
import com.dmdev.util.TestDataImporter;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.graph.SubGraph;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.LockModeType;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class HibernateRunner {

    private static final Logger log = LoggerFactory.getLogger(HibernateRunner.class);

    public static void main(String[] args) {

        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession();
             Session session1 = sessionFactory.openSession();) {

//            TestDataImporter.importData(sessionFactory);

            session.beginTransaction();
            session1.beginTransaction();

            session.createQuery("select p from Payment p", Payment.class)
                    .setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
                    .setHint("javax.persistence.lock.timeout", 5000)
                    .list();

            Payment payment = session.get(Payment.class, 1L, LockMode.PESSIMISTIC_READ);
            payment.setAmount(payment.getAmount() + 10);

            Payment payment1 = session1.get(Payment.class, 1L);
            payment1.setAmount(payment.getAmount() + 20);

            session1.getTransaction().commit();
            session.getTransaction().commit();
        }
    }
}
