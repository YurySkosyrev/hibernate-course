package com.dmdev;

import com.dmdev.entity.Payment;
import com.dmdev.entity.User;
import com.dmdev.interceptor.GlobalInterceptor;
import com.dmdev.util.HibernateUtil;
import com.dmdev.util.TestDataImporter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.jpa.QueryHints;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Slf4j
public class HibernateRunner {

    @Transactional
    public static void main(String[] args) throws SQLException {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) {
//            TestDataImporter.importData(sessionFactory);

            User user = null;
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

             user = session.find(User.class, 1L);

             user.getCompany().getName();
             user.getUserChats().size();
             User user1 = session.find(User.class, 1L);

             session.createQuery("select p from Payment p where p.receiver.id = :userId" , Payment.class)
                     .setParameter("userId", 1L)
//                     .setCacheable(true)
                     .setCacheRegion("queries")
                     .setHint(QueryHints.HINT_CACHEABLE, true)
                     .getResultList();

                System.out.println(sessionFactory.getStatistics());

                session.getTransaction().commit();
            }

            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                User user2 = session.find(User.class, 1L);
                user2.getCompany().getName();
                user2.getUserChats().size();

                session.createQuery("select p from Payment p where p.receiver.id = :userId" , Payment.class)
                        .setParameter("userId", 1L)
//                     .setCacheable(true)
                        .setCacheRegion("queries")
                        .setHint(QueryHints.HINT_CACHEABLE, true)
                        .getResultList();

                session.getTransaction().commit();
            }
        }
    }
}
