## Введение

Основные проблемы при использовании JDBC:

1. Несоответствие двух моделей. ООП (Java) и реляционная модель(СУБД)
2. Ручное написание простейших SQL запросов.
3. Отложенная загрузка данных (lazy loading). Круговая ссылка сущностей.
4. Наследование в классах. Как мапить на БД?
5. Сравнение объектов (по первичному ключу в СУБД и equals Java)
6. Кэширование. Исправление проблем Performance нашего приложения.
7. Управление транзакциями. Контроль за соединениями и запросами к БД (try, catch)

![alt text](img/orm.jpg "jdbc-structure")

ORM (Object Relational Mapping) - процесс преобразования объектно-ориентированной модели в реляционную и наоборот.

Hibernate - инструмент, который автоматизирует процесс преобразования объектно-ориентированной модели в реляционную и наоборот (ORM Framework).

## Настройка проекта

```java
dependencies {

    //зависимость используется в Compile и Runtime фазах. В тесте и в исходном 
    implementation 'org.hibernate:hibernate-core:6.1.7.Final' 
    runtimeOnly 'org.postgresql:postgresql:42.6.0'

    //после компиляции Lombok не нужен он формирует java-код
    compileOnly 'org.projectlombok:lombok:1.18.26'
    // Lombok это в основном аннотации
    annotationProcessor 'org.projectlombok:lombok:1.18.26'

    // нужны и на стадии тестов
    testCompileOnly 'org.projectlombok:lombok:1.18.26'
    testAnnotationProcessor 'org.projectlombok:lombok:1.18.26'

    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.1'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.8.1'
}
```
username VARCHAR(128) PRIMARY KEY - не автогенерируемый primary key, лучше не использовать.

Gradle устанавливать не нужно, т.к. у нас есть gradle-wrapper, который устанавливается автоматически Idea из коробки.

## Конфигурация SessionFactory

Для конфигурации SessionFactory в Hibernate используется файл hibernate.cfg.xml

```java
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-configuration PUBLIC
    "-//Hibernate/Hibernate Configuration DTD//EN"
    "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
  <session-factory>
    <property name="connection.url">jdbc:postgresql://localhost:5433/postgres</property>
    <property name="connection.username">postgres</property>
    <property name="connection.password">postgres</property>
    <property name="connection.driver_class">org.postgresql.Driver</property>
    <property name="hibernate.dialect">org.hibernate.dialect.PostgreSQLDialect</property>

    <!-- DB schema will be updated if needed -->
    <!-- <property name="hibernate.hbm2ddl.auto">update</property> -->
  </session-factory>
</hibernate-configuration>
```

dialect позволяет Hibernate сконфигурировать дополнительные типы, функции, view и так далее, которые специфичны для определенной БД.

```java
 public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.configure();

        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            System.out.println("OK");
        }
    }
```

В классе Configurate содержится всё, что нужно для создания SessionFactory - аналога ConnectionPool из JDBC.

Как и у ConnectionPool должен быть один объект SessionFactory на всё приложение.

Session - обертка над классом Connection.

## Entity

Для того, чтобы класс стал сущностью необходимо несколько правил:

- POJO - Plain Old Java Object. Все поля private, getter, setter ко всем полям.
- сущность не может быть Immutable, то есть нельзя объявлять final поля.
- сам класс не может быть final, потому что Hibernate работает с Proxy.<BR> 
Proxy работает по принципе CGLIB - code generation library, который создает наследников нашего класса
- должен быть конструктор без параметров. Т.к. Hibernate использует Reflection API для создания сущностей и последующей инициализации через setter или напрямую через Reflection API устанавливает свойства для полей.





