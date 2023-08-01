package com.dmdev;

import com.dmdev.dao.CompanyRepository;
import com.dmdev.dao.PaymentRepository;
import com.dmdev.dao.UserRepository;
import com.dmdev.dto.UserCreateDto;
import com.dmdev.entity.PersonalInfo;
import com.dmdev.entity.Role;
import com.dmdev.interceptor.TransactionInterceptor;
import com.dmdev.mapper.CompanyReadMapper;
import com.dmdev.mapper.UserCreateMapper;
import com.dmdev.mapper.UserReadMapper;
import com.dmdev.service.UserService;
import com.dmdev.util.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.lang.model.element.Element;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.time.LocalDate;

@Slf4j
public class HibernateRunner {

    @Transactional
    public static void main(String[] args) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) {

            Session session = (Session) Proxy.newProxyInstance(SessionFactory.class.getClassLoader(), new Class[]{Session.class},
                    (proxy, method, args1) -> method.invoke(sessionFactory.getCurrentSession(), args1));

//            session.beginTransaction();

            CompanyRepository companyRepository = new CompanyRepository(session);

            CompanyReadMapper companyReadMapper = new CompanyReadMapper();
            UserReadMapper userReadMapper = new UserReadMapper(companyReadMapper);
            UserCreateMapper userCreateMapper = new UserCreateMapper(companyRepository);

            UserRepository userRepository = new UserRepository(session);
            PaymentRepository paymentRepository = new PaymentRepository(session);
//            UserService userService = new UserService(userRepository, userReadMapper, userCreateMapper);

            TransactionInterceptor transactionInterceptor = new TransactionInterceptor(sessionFactory);

            UserService userService = new ByteBuddy()
                    .subclass(UserService.class) // создание субкласса
                    .method(ElementMatchers.any()) // все методы должны быть проверены на наличие аннотации
                    .intercept(MethodDelegation.to(transactionInterceptor)) // все методы должны использвоать интерсептор, можно создать один бин
                    .make() // возвращает объект, который можно загрузить в память
                    .load(UserService.class.getClassLoader()) // передаём class-loader, который загружает класс в JVM
                    .getLoaded() // получаем загруженный класс
                    .getDeclaredConstructor(UserRepository.class, UserReadMapper.class, UserCreateMapper.class) // получаем у него конструктор, который принимает все аргументы
                    .newInstance(userRepository, userReadMapper, userCreateMapper); // создаем объект этого класса.

//            userService.findById(1L).ifPresent(System.out::println);

            UserCreateDto userCreateDto = new UserCreateDto(
                    PersonalInfo.builder()
                            .firstname("Liza")
                            .lastname("Stepanova")
                            .birthDate(LocalDate.now())
                            .build(),
                    "liza2@gmail.com",
                    null,
                    Role.USER,
                    1
            );
            userService.create(userCreateDto);

//            session.getTransaction().commit();
        }
    }
}
