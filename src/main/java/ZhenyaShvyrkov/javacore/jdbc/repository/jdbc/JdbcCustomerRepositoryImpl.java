package main.java.ZhenyaShvyrkov.javacore.jdbc.repository.jdbc;

import main.java.ZhenyaShvyrkov.javacore.jdbc.model.Account;
import main.java.ZhenyaShvyrkov.javacore.jdbc.model.Customer;
import main.java.ZhenyaShvyrkov.javacore.jdbc.model.Specialty;
import main.java.ZhenyaShvyrkov.javacore.jdbc.repository.AccountRepository;
import main.java.ZhenyaShvyrkov.javacore.jdbc.repository.CustomerRepository;
import main.java.ZhenyaShvyrkov.javacore.jdbc.repository.SpecialtyRepository;
import java.sql.*;
import java.util.*;

public class JdbcCustomerRepositoryImpl implements CustomerRepository {
    private static final String URL = "jdbc:postgresql://localhost:5432/crud?user=postgres&password=251084";
    private static final String DRIVER = "org.postgresql.Driver";
    private static CustomerRepository jdbcCustomerRepository;
    private static AccountRepository jdbcAccountRepository;
    private static SpecialtyRepository jdbcSpecialtyRepository;
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;
    private long customerId;


    private JdbcCustomerRepositoryImpl() {}

    public static synchronized CustomerRepository getJdbcCustomerRepository(){
        if (jdbcCustomerRepository == null) {
            jdbcCustomerRepository = new JdbcCustomerRepositoryImpl();
        }
        return jdbcCustomerRepository;
    }

    static {
        Statement statementForCreatingTable = null;
        try {
            Class.forName(DRIVER);
            jdbcSpecialtyRepository = JdbcSpecialtyRepositoryImpl.getJdbcSpecialtyRepository();
            jdbcAccountRepository = JdbcAccountRepositoryImpl.getRepository();
            connection = getConnection();
            statement = connection.createStatement();
            statementForCreatingTable = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS customer(" +
                    "customer_id BIGINT UNIQUE NOT NULL PRIMARY KEY," +
                    "first_name VARCHAR(64) NOT NULL," +
                    "last_name VARCHAR(64) NOT NULL," +
                    "age INT NOT NULL," +
                    "customer_status VARCHAR(7) NOT NULL)");
            statementForCreatingTable.execute("CREATE TABLE IF NOT EXISTS customer_to_specialty(" +
                    "customer_id BIGSERIAL REFERENCES customer (customer_id)," +
                    "specialty_id BIGSERIAL REFERENCES specialty (specialty_id)," +
                    "PRIMARY KEY (customer_id, specialty_id))");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, statement);
            try {
                statementForCreatingTable.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Customer save(Customer customer) {
        connection = getConnection();
        findMaxId();
        Set<Specialty> specialtySet = new HashSet<>();
        try {
            Account account = customer.getAccount();
            //long specialtyId = findMaxId();
            preparedStatement = connection.prepareStatement("INSERT INTO customer VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setLong(1, ++customerId);
            preparedStatement.setString(2, account.getFirstName());
            preparedStatement.setString(3, account.getLastName());
            preparedStatement.setInt(4, account.getAge());
            preparedStatement.setString(5, account.getStatus().toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            jdbcAccountRepository.save(customer.getAccount());
            for (Specialty oldSpecialty : customer.getSpecialties()) {
                Specialty specialty = jdbcSpecialtyRepository.save(oldSpecialty);
                preparedStatement = connection.prepareStatement("INSERT INTO customer_to_specialty VALUES (?, ?)");
                preparedStatement.setLong(1, customerId);
                preparedStatement.setLong(2, specialty.getId());
                preparedStatement.executeUpdate();
                specialtySet.add(specialty);
            }
            customer.getSpecialties().clear();
            customer.getSpecialties().addAll(specialtySet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, preparedStatement);
        }
        return customer;
    }

    @Override
    public List<Customer> read() {
        connection = getConnection();
        List<Customer> customers = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customer");
            while (resultSet.next()) {
                customerId = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                int age = resultSet.getInt(4);
                Account.AccountStatus status = toStatus(resultSet.getString(5));
                preparedStatement = connection.prepareStatement("SELECT specialty_id FROM customer_to_specialty WHERE customer_id = ?");
                preparedStatement.setLong(1, customerId);
                ResultSet resultSet1 = preparedStatement.executeQuery();
                Set<Specialty> specialtySet = new HashSet<>();
                while (resultSet1.next()){
                    long id = resultSet1.getLong(1);
                    Specialty specialty = jdbcSpecialtyRepository.readById(id);
                    specialtySet.add(specialty);
                }
                Account account = new Account(firstName, lastName, age, status);
                Customer customer = new Customer(account, specialtySet);
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, statement, preparedStatement);
        }
        return customers;
    }

    @Override
    public Customer readById(Long id) {
        connection = getConnection();
        Set<Specialty> specialties = new HashSet<>();
        Customer customer = null;
        PreparedStatement preparedStatementSp = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM customer WHERE customer_id = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                customerId = resultSet.getLong(1);
                String name = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                int age = resultSet.getInt(4);
                Account.AccountStatus status = toStatus(resultSet.getString(5));
                preparedStatementSp = connection.prepareStatement("SELECT specialty_id" +
                        " FROM customer_to_specialty WHERE customer_id = ?");
                preparedStatementSp.setLong(1, customerId);
                ResultSet specialtiesId = preparedStatementSp.executeQuery();
                while (specialtiesId.next()){
                    long specialtyId = specialtiesId.getLong(1);
                    Specialty specialty = jdbcSpecialtyRepository.readById(specialtyId);
                    specialty.setId(specialtyId);
                    specialties.add(specialty);
                }
                Account account = new Account(name, lastName, age, status);
                customer = new Customer(account, specialties);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, preparedStatement, preparedStatementSp);
        }
        return customer;
    }

    @Override
    public Customer update(Customer customer, Long id) {
        connection = getConnection();
        long specialtyId;
        //PreparedStatement preparedStatement2 = null;
        //PreparedStatement preparedStatement3 = null;
        try {
            preparedStatement = connection.prepareStatement("UPDATE customer SET" +
                    " first_name = ?," +
                    " last_name = ?," +
                    " age = ?," +
                    " customer_status = ?" +
                    "WHERE customer_id = ?");
            jdbcAccountRepository.update(customer.getAccount(), id);
            preparedStatement.setString(1, customer.getAccount().getFirstName());
            preparedStatement.setString(2, customer.getAccount().getLastName());
            preparedStatement.setInt(3, customer.getAccount().getAge());
            preparedStatement.setString(4, customer.getAccount().getStatus().toString());
            preparedStatement.setLong(5, id);
            preparedStatement.executeUpdate();
            PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT specialty_id" +
                    " FROM customer_to_specialty WHERE customer_id = ?");
            preparedStatement2.setLong(1, id);
            ResultSet resultSet = preparedStatement2.executeQuery();
            Iterator<Specialty> iterator = customer.getSpecialties().iterator();
            PreparedStatement preparedStatement4 = connection.prepareStatement("SELECT COUNT(*) FROM customer_to_specialty WHERE customer_id = ?");
            preparedStatement4.setLong(1, id);
            ResultSet count = preparedStatement4.executeQuery();
            count.next();
            long resultSetSize = count.getLong(1);
            if (resultSetSize >= customer.getSpecialties().size()) {
                while (resultSet.next()) {
                    specialtyId = resultSet.getLong(1);
                    if (iterator.hasNext()) {
                        Specialty specialty = iterator.next();
                        jdbcSpecialtyRepository.update(specialty, specialtyId);
                    } else {
                        PreparedStatement preparedStatement3 = connection.prepareStatement("DELETE FROM customer_to_specialty WHERE specialty_id = ?");
                        preparedStatement3.setLong(1, specialtyId);
                        preparedStatement3.execute();
                        jdbcSpecialtyRepository.deleteByID(specialtyId);
                    }
                }
            } else {
                while (iterator.hasNext()) {
                    Specialty specialty = iterator.next();
                    if (resultSet.next()) {
                        specialtyId = resultSet.getLong(1);
                        jdbcSpecialtyRepository.update(specialty, specialtyId);
                    } else {
                        Specialty specialty1 = jdbcSpecialtyRepository.save(specialty);
                        PreparedStatement preparedStatement5 = connection.prepareStatement("INSERT INTO customer_to_specialty VALUES (?, ?)");
                        preparedStatement5.setLong(1, id);
                        preparedStatement5.setLong(2, specialty1.getId());
                        preparedStatement5.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, preparedStatement);
            //assert preparedStatement3 != null;
            //JdbcUtils.closeQuietly(preparedStatement2, preparedStatement3);
        }
        return customer;
    }

    @Override
    public void delete(Customer customer) {
        connection = getConnection();
        try {
            jdbcAccountRepository.delete(customer.getAccount());
            preparedStatement = connection.prepareStatement("UPDATE customer SET customer_status = ? WHERE first_name = ? " +
                    "AND last_name = ? " +
                    "AND age = ? " +
                    "AND customer_status = ?", PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, "DELETED");
            preparedStatement.setString(2, customer.getAccount().getFirstName());
            preparedStatement.setString(3, customer.getAccount().getLastName());
            preparedStatement.setInt(4, customer.getAccount().getAge());
            preparedStatement.setString(5, customer.getAccount().getStatus().toString());
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            customerId = resultSet.getLong(1);
            PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT specialty_id FROM customer_to_specialty WHERE customer_id = ?");
            preparedStatement2.setLong(1, customerId);
            ResultSet resultSetOfSpecialtiesId = preparedStatement2.executeQuery();
            PreparedStatement preparedStatement3 = connection.prepareStatement("DELETE FROM customer_to_specialty WHERE customer_id = ?");
            preparedStatement3.setLong(1, customerId);
            preparedStatement3.execute();
            while (resultSetOfSpecialtiesId.next()) {
                jdbcSpecialtyRepository.deleteByID(resultSetOfSpecialtiesId.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, preparedStatement);
        }
    }

    @Override
    public void deleteByID(Long id) {
        connection = getConnection();
        try {
            jdbcAccountRepository.deleteByID(id);
            preparedStatement = connection.prepareStatement("SELECT specialty_id FROM customer_to_specialty WHERE customer_id = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            PreparedStatement preparedStatement2 = connection.prepareStatement("DELETE FROM customer_to_specialty WHERE customer_id = ?");
            preparedStatement2.setLong(1, id);
            preparedStatement2.execute();
            preparedStatement = connection.prepareStatement("UPDATE customer  SET customer_status = ? WHERE customer_id = ?");
            preparedStatement.setString(1, "DELETED");
            preparedStatement.setLong(2, id);
            preparedStatement.execute();
            while (resultSet.next()) {
                long specialtyId = resultSet.getLong(1);
                jdbcSpecialtyRepository.deleteByID(specialtyId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, preparedStatement);
        }

    }

    private void findMaxId()  {
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(customer_id) FROM customer");
            if (resultSet.next()) {
                customerId = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() {
        try {
            connection = DriverManager.getConnection(URL);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    private static Account.AccountStatus toStatus(String status) {
        Account.AccountStatus accountStatus = null;
        if (status.equalsIgnoreCase("active")) {
            accountStatus = Account.AccountStatus.ACTIVE;
        } else if (status.equalsIgnoreCase("banned")) {
            accountStatus = Account.AccountStatus.BANNED;
        } else if (status.equalsIgnoreCase("deleted")) {
            accountStatus = Account.AccountStatus.DELETED;
        }
        return accountStatus;
    }
}
