package com.dmdev.dao;

import com.dmdev.dto.PaymentFilter;
import com.dmdev.entity.Company;
import com.dmdev.entity.Payment;
import com.dmdev.entity.User;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static com.dmdev.entity.QCompany.company;
import static com.dmdev.entity.QPayment.payment;
import static com.dmdev.entity.QUser.user;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    /**
     * Возвращает всех сотрудников
     */
    public List<User> findAll(Session session) {
//        return session.createQuery("select u from User u", User.class)
//                .list();

//        CriteriaBuilder cb = session.getCriteriaBuilder();
//        CriteriaQuery<User> criteria = cb.createQuery(User.class);
//        Root<User> user = criteria.from(User.class);
//        criteria.select(user);

//        return session.createQuery(criteria)
//                .list();

        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .fetch(); // аналог list()
    }

    /**
     * Возвращает всех сотрудников с указанным именем
     */
    public List<User> findAllByFirstName(Session session, String firstName) {
//        return session.createQuery("select u from User u where u.personalInfo.firstname = :firstName", User.class)
//                .setParameter("firstName", firstName)
//                .list();

//        CriteriaBuilder cb = session.getCriteriaBuilder();
//        CriteriaQuery<User> criteria = cb.createQuery(User.class);
//        Root<User> user = criteria.from(User.class);
//
//        criteria.select(user).where(
//                cb.equal(user.get(User_.personalInfo).get(PersonalInfo_.firstname), firstName));
//
//        return session.createQuery(criteria)
//                .list();

        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .where(user.personalInfo.firstname.eq(firstName))
                .fetch();
    }

    /**
     * Возвращает первые {limit} сотрудников, упорядоченных по дате рождения (в порядке возрастания)
     */
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
//        return session.createQuery("select u from User u order by u.personalInfo.birthDate", User.class)
//                .setMaxResults(limit)
//                .list();

//        CriteriaBuilder cb = session.getCriteriaBuilder();
//        CriteriaQuery<User> criteria = cb.createQuery(User.class);
//        Root<User> user = criteria.from(User.class);
//
//        criteria.select(user).orderBy(cb.asc(user.get(User_.personalInfo).get(PersonalInfo_.birthDate)));
//
//        return session.createQuery(criteria)
//                .setMaxResults(limit)
//                .list();

        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .orderBy(user.personalInfo.birthDate.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * Возвращает всех сотрудников компании с указанным названием
     */
    public List<User> findAllByCompanyName(Session session, String companyName) {
//        return session.createQuery("select u from Company c " +
//                        "join c.users u where c.name = :companyName", User.class)
//                .setParameter("companyName", companyName)
//                .list();
//        CriteriaBuilder cb = session.getCriteriaBuilder();
//        CriteriaQuery<User> criteria = cb.createQuery(User.class);
//        Root<Company> company = criteria.from(Company.class);
//        MapJoin<Company, String, User> users = company.join(Company_.users);
//
//        criteria.select(users).where(cb.equal(company.get(Company_.name), companyName));
//
//        return session.createQuery(criteria)
//                .list();

        return new JPAQuery<User>(session)
                .select(user)
                .from(company)
                .join(company.users, user)
                .where(company.name.eq(companyName))
                .fetch();
    }

    /**
     * Возвращает все выплаты, полученные сотрудниками компании с указанными именем,
     * упорядоченные по имени сотрудника, а затем по размеру выплаты
     */
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
//        return session.createQuery("select p from Payment p " +
//                        "join p.receiver u " +
//                        "join u.company c " +
//                        "where c.name = :companyName " +
//                        "order by u.personalInfo.firstname, p.amount", Payment.class)
//                .setParameter("companyName", companyName)
//                .list();

//        CriteriaBuilder cb = session.getCriteriaBuilder();
//        CriteriaQuery<Payment> criteria = cb.createQuery(Payment.class);
//        Root<Payment> payment = criteria.from(Payment.class);
//        Join<Payment, User> user = payment.join("receiver");
//        payment.fetch("receiver");
//
//        Join<User, Company> company = user.join("company");
//
//        criteria.select(payment).where(
//                cb.equal(company.get("name"), companyName)
//        )
//                .orderBy(
//                        cb.asc(user.get("personalInfo").get("firstname")),
//                        cb.asc(payment.get("amount"))
//                );

//        return session.createQuery(criteria)
//                .list();

        return new JPAQuery<Payment>(session)
                .select(payment)
                .from(payment)
                .join(payment.receiver, user).fetchJoin() // благодаря второму параметру можем обращаться к user
                .join(user.company, company)
                .where(company.name.eq(companyName))
                .orderBy(user.personalInfo.firstname.asc(), payment.amount.asc())
                .fetch();
    }

    /**
     * Возвращает среднюю зарплату сотрудника с указанными именем и фамилией
     */
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, PaymentFilter filter) {
//        return session.createQuery("select avg(p.amount) from Payment p " +
//                        "join p.receiver u where u.personalInfo.firstname = :firstName and " +
//                        "u.personalInfo.lastname = :lastName", Double.class)
//                .setParameter("firstName", firstName)
//                .setParameter("lastName", lastName)
//                .uniqueResult();

//        CriteriaBuilder cb = session.getCriteriaBuilder();
//        CriteriaQuery<Double> criteria = cb.createQuery(Double.class);
//
//        Root<Payment> payment = criteria.from(Payment.class);
//        Join<Payment, User> user = payment.join(Payment_.receiver);
//
//        List<Predicate> predicates = new ArrayList<>();
//        if (firstName != null) {
//            predicates.add(cb.equal(user.get(User_.personalInfo).get(PersonalInfo_.firstname), firstName));
//        }
//        if (lastName != null) {
//            predicates.add(cb.equal(user.get(User_.personalInfo).get(PersonalInfo_.lastname), lastName));
//        }
//
//        criteria.select(cb.avg(payment.get(Payment_.amount))).where(predicates.toArray(Predicate[]::new));
//
//        return session.createQuery(criteria)
//                .uniqueResult();

//        List<Predicate> predicates = new ArrayList<>();
//        if (filter.getFirstName() != null) {
//            predicates.add(user.personalInfo.firstname.eq(filter.getFirstName()));
//        }
//        if (filter.getLastName() != null) {
//            predicates.add(user.personalInfo.lastname.eq(filter.getLastName()));
//        }

        Predicate predicate = QPredicate.builder()
                .add(filter.getFirstName(), user.personalInfo.firstname::eq)
                .add(filter.getLastName(), user.personalInfo.lastname::eq)
                .buildAnd();

        return new JPAQuery<Double>(session)
                .select(payment.amount.avg())
                .from(payment)
                .join(payment.receiver, user)
                .where(predicate)
                .fetchOne();
    }

    /**
     * Возвращает для каждой компании: название, среднюю зарплату всех её сотрудников. Компании упорядочены по названию.
     */
    public List<Tuple> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
//        return session.createQuery("select c.name, avg(p.amount) from Company c " +
//                        "join c.users u " +
//                        "join u.payments p " +
//                        "group by c.name " +
//                        "order by c.name", Object[].class)
//        .list();

//        CriteriaBuilder cb = session.getCriteriaBuilder();
//        CriteriaQuery<CompanyDto> criteria = cb.createQuery(CompanyDto.class);
//
//        Root<Company> company = criteria.from(Company.class);
//        MapJoin<Company, String, User> user = company.join(Company_.users, JoinType.INNER);
//        ListJoin<User, Payment> payment = user.join(User_.payments);
//
//        criteria.select(
//                        cb.construct(CompanyDto.class,
//                company.get(Company_.name),
//                cb.avg(payment.get(Payment_.amount)))
//                )
//                .groupBy(company.get(Company_.name))
//                .orderBy(cb.asc(company.get(Company_.name)));
//
//        return session.createQuery(criteria)
//                .list();

        return new JPAQuery<Tuple>(session)
                .select(company.name, payment.amount.avg())
                .from(company)
                .join(company.users, user)
                .join(user.payments, payment)
                .groupBy(company.name)
                .orderBy(company.name.asc())
                .fetch();

    }

    /**
     * Возвращает список: сотрудник (объект User), средний размер выплат, но только для тех сотрудников, чей средний размер выплат
     * больше среднего размера выплат всех сотрудников
     * Упорядочить по имени сотрудника
     */
    public List<Tuple> isItPossible(Session session) {
//        return session.createQuery("select u, avg(p.amount) from User u " +
//                        "join u.payments p " +
//                        "group by u " +
//                        "having avg(p.amount) > (select avg (p.amount) from Payment p) " +
//                        "order by u.personalInfo.firstname", Object[].class)
//                .list();

//        CriteriaBuilder cb = session.getCriteriaBuilder();
//
//        CriteriaQuery<Tuple> criteria = cb.createQuery(Tuple.class);
//        Root<User> user = criteria.from(User.class);
//        ListJoin<User, Payment> payment = user.join(User_.payments);
//
//        Subquery<Double> subQuery = criteria.subquery(Double.class);
//        Root<Payment> paymentSubQuery = subQuery.from(Payment.class);
//
//
//
//        criteria.select(
//                cb.tuple(
//                        user,
//                        cb.avg(payment.get(Payment_.amount))
//                )
//        )
//                .groupBy(user.get(User_.id))
//                .having(cb.gt(cb.avg(payment.get(Payment_.amount)),
//                        subQuery.select(cb.avg(paymentSubQuery.get(Payment_.amount)))
//                        ))
//                .orderBy(cb.asc(user.get(User_.personalInfo).get(PersonalInfo_.firstname)));
//
//        return session.createQuery(criteria)
//                .list();
        return new JPAQuery<Tuple>(session)
                .select(user, payment.amount.avg())
                .from(user)
                .join(user.payments, payment)
                .groupBy(user.id)
                .having(payment.amount.avg().gt(new JPAQuery<Double>(session)
                        .select(payment.amount.avg())
                        .from(payment)))
                .orderBy(user.personalInfo.firstname.asc())
                .fetch();

    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}