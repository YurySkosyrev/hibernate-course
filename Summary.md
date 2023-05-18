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

@Entity - аннотация, чтобы класс стал Hibernate-сущностью. Каждая сущность должна иметь Id, поэтому одно поле
нужно пометить аннотацией @Id

Поле @Id должно быть serializable.

В Hibernate необходимо вручную работать с транзакциями 
```java
 public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.configure();

        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            User user = User.builder()
                    .username("ivan@mail.ru")
                    .firstname("Ivan")
                    .lastname("Ivanov")
                    .birthDate(LocalDate.of(2000, 1, 12))
                    .age(20)
                    .build();

            session.save(user);
            session.getTransaction().commit();
        }
    }
```

Чтобы Hibernate отслеживал сущность необходимо необходимо добавить соответствующий класс в конфигурацию

```java
 configuration.addAnnotatedClass(User.class);
```

Либо прописать mapping класса в xml:
```java
  <mapping class="com.dmdev.entity.User"/>
```

@Table(name = "Users") - указываем к какой таблице относится данная сущность.

Чтобы настроить маппинг названия полей класса и колонок в БД, необходимо установить

```java
configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
```

Либо использовать аннотацию @Column(name = "birth_day"). Здесь так же можно указать много информации: nullable, точность, можно вставлять или нет, размер. На основании этой метаинформации Hibernate поддерживает автосоздание DDL.

## Класс Session

Интерфейс Session по сути обертка над классом Connection, которая работает с сущностями и отслеживает их жизненный цикл.

При вызове метода save() с помощью ReflectionAPI формируется SQL-запрос.

```java
  @Test
    void checkReflectionApi() throws SQLException, IllegalAccessException {
        User user = User.builder()
                .username("ivan@mail.ru")
                .firstname("Ivan")
                .lastname("Ivanov")
                .birthDate(LocalDate.of(2000, 1, 12))
                .age(20)
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
```

В Hibernate это всё происходит автоматически под капотом, достаточно лишь правильно описать сущность в соответствующем классе.

## Type Converter

В классе Configuration есть поле 
'''java
private final List<BasicType<?>> basicTypes = new ArrayList<>();
'''

Это список всех типов, которые может поддерживать Hibernate и преобразовывать один в другой.
Есть методы wrap() и unwrap(), которые преобразуют типы Java в типы SQL и наоборот.
А так же Descriptorы, которые устанавливают значения в JDBC запрос.

Enum можно хранить в БД в виде String (EnumType.STRING) или int (EnumType.ORDINAL). int плохой способ хранения Enum, так как при изменении
порядка значений смысловая сущность записанных данных в БД будет утеряна.

Все Enum наследуются от класса Enum из пакета java.lang, поэтому нельзя наследовать другие классы, а только реализовывать интерфейсы.

```java
    @Enumerated(EnumType.STRING) // по умолчанию EnumType.ORDINAL, т.е. Enum преобразуется в int
    private Role role;
```

По сути преобразование Java классов в SQL типы происходит с помощью соответствующих реализаций interface Type.

## Custom attribute converter

Если нужно преобразовать свой класс в типы SQL можно реализовать свой Converter

В новых классах работы с датами нет методов перехода к старым, а в старых есть, чтобы когда-нибудь отказаться от старых типов.

```java
public class BirthDayConverter implements AttributeConverter<Birthday, Date> {
    @Override
    public Date convertToDatabaseColumn(Birthday attribute) {
        return Optional.ofNullable(attribute)
                .map(Birthday::birthDate)
                .map(Date::valueOf)
                .orElse(null);
    }

    @Override
    public Birthday convertToEntityAttribute(Date dbData) {
        return Optional.ofNullable(dbData)
                .map(Date::toLocalDate)
                .map(Birthday::new)
                .orElse(null);
    }
}


@Convert(converter = BirthDayConverter.class)
@Column(name = "birth_date")
private Birthday birthDate;
```
Чтобы не прописывать @Convert(converter = BirthDayConverter.class) в сущность, можно настроить конфигурацию Hibernate.

```java
 configuration.addAttributeConverter(new BirthDayConverter(), true);
```

Либо над конвертером
```java
@Converter(autoApply = true)
public class BirthDayConverter implements AttributeConverter<Birthday, Date> { ...
}
```

## Custom user type

Предположим в БД есть колонка типа JSONB, которой нет аналога в Java-SQL. Тогда нам нужно создать свой собственный тип реализуя 
интерфейс Type, либо UserType.

Главные методы в них это nullSafeGet и nullSafeSet, которые работают с ResultSet и устанвливают значения в PrepareStatement.

Чтобы не переопределять все методы вручную можно воспользоваться библиотекой Hibernate Types

```java
public class User {
        ...
    //    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonBinaryType")
        @Type(type = "jsonb")
        private String info;
}
```

Так же нужно зарегистрировать тип в configuration
```java
 configuration.registerTypeOverride(new JsonBinaryType());
```

при этом объект просто добавляется в коллекцию basicTypes.

Можно указать короткое имя над классом (пакетом) 

```java
@TypeDef(name = "dmdev", typeClass = JsonBinaryType.class)
public class User {
    ...

    @Type(type = "dmdev")
    private String info;
}
```

## Методы update, delete, get

Метод Session.update() обновляет пользователя, если его нет, то пробрасывает exception.

В Hibernate отложенная отправка запросов, он старается максимально отодвинуть момент открытия транзакции и общения с БД. Чтобы можно было собрать несколько SQL-запросов и отправить их batch. Поэтому запросы выполняются после того как транзакция будет закомичена.

Метод Session.saveOrUpdate() сначала делает запрос select и потом так же откладывает вставку или обновление до коммита.

Метод Session.delete() удаляет пользователя по его идентификатору. У каждой сущность должно быть поле id, чтобы не нарушать первую нормальную форму. При этом так же сначала выполняется select-запрос по идентификатору, а потом уже отложенное удаление.

Session.get(User.class, id) - возвращает сущность по id. Передаётся именно пара - класс и id, т.к. id могут дублироваться в разных таблицах.

```java
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
```

Необходимо анализировать аннотации чтобы смапить значения из ResultSet в поля сущности, а так же необходимые конвертеры, чтобы преобразовать SQL-типы в соответствующий тип поля сущности.

Универсальность в подобных фреймворках достигает за счёт ReflectionAPI. Нам всё равно как мы создаём наши классы, не нужно переопределять интерфейсы, наследоваться от готовых классов в Hibernate или Spring. Достаточно создать сущность и расставить нужные аннотации - добавить мета-информацию, всё остальное Hibernate сделает за нас.

## Entity persister

Любая Session знает о SessionFactory, которая её создала. С помощью метода getFactory().getMetamodel() мы получаем 
метаданные о сессии и сущностях. В Map EntityPersisterMap хранится полное имя класса как ключ и соответствующий SingleTableEntityPersister. Именно он загружает, сохраняет, обновляет в нашу БД сущность.

Так же в метаданных хранится объект TypeConfiguration, в которой содержится информация о всех типах и реализаций их UserType.

EntityPersister мапит SQL запросы с нашей сущностью.

![alt text](img/persister.jpg "jdbc-structure")

Создаётся SessionFactory - один единственный объект на всё приложение. В нём есть MetaModel, которая строится на основании всех классов и сущностей, которые мы туда добавили. Плюс BasicTypes загруженные по умолчанию из Hibernate для стандартных типов данных, либо созданные нами. На каждую сущность, которая обязана иметь id мапится EntityPersister, он знает как совершать CRUD операции для каждой сущности.

В Hibernate широко используется модель event - listner, то есть каждый listner вызывается при возникновении определенного event.

## First Level Cashe

Класс сущности является ключем по которому мы получаем соответствующий EntityPersister. 

В Hibernate реализованы механизмы кэширования, чтобы меньше раз обращаться к БД. Это First Level Cashe, он есть всегда по умолчанию и никак его не отключить. 

В объекте Session есть поле persistenceContext - кэш. В объекте класса PersistenceContext есть ссылка на сессию, которой он принадлежит. У каждой сессии свой PersistenceCentext. 

В PersistenceContext есть ассоциативный массив entitiesByKey, ключ - объект EntityKey с полями identifier - id, hashcode, entityPersister.
значение - объект который получен из БД.

Сущности в persistenceContext помещаются после вызова методов у объекта Session - save, get и другие.

Для удаления объектов из кэша есть два метода:

- Session.evict(user) - удаление одного объекта
- Session.clear() - удаление всех объектов
- Session.close() - закрыть сессию

Если после получения сущности из БД мы поменяем в ней поля, то эти изменения отразятся и на БД. То есть перед commit будет вызван 
запрос update.

```java
    User user = session.get(User.class, "ivan@mail.ru");
    user.setLastname("Sidorov");
```

Таким образом изменения во всех сущностях, содержащихся в persistenceContext будут автоматически Hibernate производится и в БД.
Это называется dirty session.

Session.isDirty() показывает были ли изменения в сущностях, ассоциированных с данной сессией.

Flush - сливание всех изменений в БД.

Session.flush() - сливает все изменения объектов persistenceContext в БД.

![alt text](img/persistenceContext.jpg "jdbc-structure")

В metaModel есть много объектов Session, нечто вроде SessionPool и в каждой есть свой PersistenceContext. Каждая сущность может быть ассоциирована со своим PersistenceContext и в каждом PersistenceContext будет своё состояние этой сущности.

![alt text](img/entityLifeCycle.jpg "jdbc-structure")

Отличие Detached от Transient, в том что сущность была в Persistent-состоянии.

@UtilityClass - создаёт private-конструктор и final-класс.








