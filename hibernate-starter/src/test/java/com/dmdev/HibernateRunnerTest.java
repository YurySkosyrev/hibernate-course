package com.dmdev;

import com.dmdev.entity.*;
import com.dmdev.util.HibernateUtil;
import lombok.Cleanup;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class HibernateRunnerTest {


    @Test
    void localeInfo() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Company company = session.get(Company.class, 1);
//            System.out.println(company.getLocales());
//            company.getLocales().add(LocaleInfo.of("ru", "Описание на русском"));
//            company.getLocales().add(LocaleInfo.of("en", "English description"));
            company.getUsers().forEach((k, v) -> System.out.println(v));
            session.getTransaction().commit();
        }
    }

    @Test
    void checkManyToMany() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            User user = session.get(User.class, 2L);
            Chat chat = session.get(Chat.class, 1L);

            UserChat userChat = UserChat.builder()
                    .created_at(Instant.now())
                    .created_by(user.getUsername())
                    .build();
            userChat.addChat(chat);
            userChat.addUser(user);

            session.save(userChat);

//            Chat chat = Chat.builder()
//                    .name("dmdev")
//                    .build();
//            session.save(chat);

            session.getTransaction().commit();
        }
    }

    @Test
    void checkOneToOne(){
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

//            User user = session.get(User.class, 6L);
//            System.out.println(user.getId());
            User user = User.builder()
                    .username("test2@gmail.com")
                    .build();

            Profile profile = Profile.builder()
                    .street("Kolasa 666")
                    .language("ru")
                    .build();

            profile.setUser(user);
            session.save(user);

            session.getTransaction().commit();
        }
    }

    @Test
    void checkOrphanRemoval(){
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Company company = session.getReference(Company.class, 1);
//            company.getUsers().removeIf(user -> user.getId() == 1L);

            session.getTransaction().commit();
        }
    }

    @Test
    void checkLazyInitialisation() {
        Company company = null;
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            company = session.get(Company.class, 1);

            session.getTransaction().commit();
        }
//        Set<User> users = company.getUsers();
//        System.out.println(users.size());
    }

    @Test
    void deleteCompany() {
        @Cleanup
        SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        Company company = session.get(Company.class, 1L);
        session.delete(company);

        session.getTransaction().commit();
    }

    @Test
    void addUserToNewCompany() {
        @Cleanup
        SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        Company company = Company.builder()
                .name("Facebook")
                .build();

        User user = User.builder()
                .username("sveta@gmail.com")
                .build();

        company.addUser(user);

        session.getTransaction().commit();
    }

    @Test
    void oneToMany() {
        @Cleanup
        SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        Company company = session.get(Company.class, 1);
        System.out.println("");

        session.getTransaction().commit();
    }

    @Test
    void checkGetReflectionApi() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.getString("username");
        resultSet.getString("lastname");

        Class<User> clazz = User.class;

        Constructor<User> constructor = clazz.getConstructor();
        User user = constructor.newInstance();
        Field usernameField = clazz.getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(user, resultSet.getString("username"));
    }

    @Test
    void checkReflectionApi() throws SQLException, IllegalAccessException {
        User user = User.builder()
                .build();

        String sql = """
                insert
                into
                %s
                (%s)
                values
                (%s)
                """;

        String tableName = Optional.ofNullable(user.getClass().getAnnotation(Table.class))
                .map(tableAnnotation -> tableAnnotation.schema() + "." + tableAnnotation.name())
                .orElse(user.getClass().getName());

        Field[] declaredFields = user.getClass().getDeclaredFields();
        String columnName = Arrays.stream(declaredFields)
                .map(field -> Optional.ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName()))
                .collect(Collectors.joining(", "));

        String columnValues = Arrays.stream(declaredFields)
                .map(fields -> "?")
                .collect(Collectors.joining(", "));

        String setSql = sql.formatted(tableName, columnName, columnValues);
        System.out.println(setSql);

        Connection connection = null;
        PreparedStatement preparedStatement = connection.prepareStatement(setSql);
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            preparedStatement.setObject(1, declaredField.get(user));
        }
    }

}