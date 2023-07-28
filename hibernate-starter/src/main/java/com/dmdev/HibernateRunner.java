package com.dmdev;

import com.dmdev.entity.Payment;
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

            try ( Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                Payment payment = session.find(Payment.class, 1L);
//                payment.setAmount(payment.getAmount() + 10);

                session.getTransaction().commit();
            }

            try ( Session session2 = sessionFactory.openSession()) {
                session2.beginTransaction();

                AuditReader auditReader = AuditReaderFactory.get(session2);
//                auditReader.find(Payment.class, 1L, 1L); // получение по id ревизии
                Payment oldPayment = auditReader.find(Payment.class, 1L, new Date(1690466465073L));// получение по timestamp

                session2.replicate(oldPayment, ReplicationMode.OVERWRITE);

                List resultList = auditReader.createQuery()
                        .forEntitiesAtRevision(Payment.class, 400L)
                        .add(AuditEntity.property("amount").ge(450))
                        .add(AuditEntity.property("id").ge(6L))
                        .addProjection(AuditEntity.property("amount"))
                        .addProjection(AuditEntity.id())
                        .getResultList();


                session2.getTransaction().commit();
            }
        }
    }

}
