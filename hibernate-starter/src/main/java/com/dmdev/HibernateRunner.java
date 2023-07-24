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
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.graph.SubGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class HibernateRunner {

    private static final Logger log = LoggerFactory.getLogger(HibernateRunner.class);

    public static void main(String[] args) {

        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        Session session = sessionFactory.openSession()) {

//            TestDataImporter.importData(sessionFactory);

            RootGraph<User> graph = session.createEntityGraph(User.class);
            graph.addAttributeNodes("company", "userChats");
            SubGraph<UserChat> subGraph = graph.addSubgraph("userChats", UserChat.class);
            subGraph.addAttributeNodes("chat");


            session.beginTransaction();
//            session.enableFetchProfile("withCompanyAndPayment");

            Map<String, Object> properties = Map.of(
                    GraphSemantic.LOAD.getJpaHintName(), session.getEntityGraph("withCompanyAndChat")
            );

            User user = session.find(User.class, 1L, properties);
            System.out.println(user.getCompany());
            System.out.println(user.getUserChats().size());


//            User user = session.get(User.class, 1L);
//            System.out.println(user.getPayments());
//            System.out.println(user.getCompany());

            List<User> users = session.createQuery(
                    "select u from User u join u.payments where 1 = 1", User.class)
//                    .setHint(GraphSemantic.LOAD.getJpaHintName(), session.getEntityGraph("withCompanyAndChat"))
                    .setHint(GraphSemantic.LOAD.getJpaHintName(), graph)
                    .list();
            users.forEach(it -> System.out.println(it.getUserChats().size()));
            users.forEach(it -> System.out.println(it.getCompany().getName()));

            session.getTransaction().commit();
        }
    }
}
