package main.java.ZhenyaShvyrkov.javacore.jdbc.repository.jdbc;

import main.java.ZhenyaShvyrkov.javacore.jdbc.model.Account;
import main.java.ZhenyaShvyrkov.javacore.jdbc.repository.AccountRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcAccountRepositoryImpl implements AccountRepository {
    private static final String URL = "jdbc:postgresql://localhost:5432/crud?user=postgres&password=251084";
    private static final String DRIVER = "org.postgresql.Driver";
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;
    private static AccountRepository repository;
    private static ResultSet resultSet;
    private static long id;

    private JdbcAccountRepositoryImpl() {}

    public static synchronized AccountRepository getRepository(){
        if (repository == null) {
            repository = new JdbcAccountRepositoryImpl();
        }
        return repository;
    }

    static {
        try {
            Class.forName(DRIVER);
            connection = getConnection();
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS account(" +
                    "account_id BIGINT UNIQUE NOT NULL PRIMARY KEY," +
                    "first_name VARCHAR(64) NOT NULL," +
                    "last_name VARCHAR(64) NOT NULL," +
                    "age INT NOT NULL," +
                    "account_status VARCHAR(7) NOT NULL)");
            preparedStatement.execute();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, preparedStatement);
        }
    }

    @Override
    public Account save(Account account) {
        connection = getConnection();
        id = findMaxId();
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO account VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setLong(1, ++id);
            preparedStatement.setString(2, account.getFirstName());
            preparedStatement.setString(3, account.getLastName());
            preparedStatement.setInt(4, account.getAge());
            preparedStatement.setString(5, account.getStatus().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, preparedStatement);
        }
        return account;
    }

    @Override
    public List<Account> read() {
        List<Account> accounts = new ArrayList<>();
        connection = getConnection();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM account");
            while (resultSet.next()) {
                id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                int age = resultSet.getInt(4);
                Account.AccountStatus status = toStatus(resultSet.getString(5));
                Account account = new Account(firstName, lastName, age, status);
                account.setId(id);
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, statement, resultSet);
        }
        return accounts;
    }

    @Override
    public Account readById(Long id) {
        Account account = null;
        connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM account WHERE account_id = ?");
            preparedStatement.setLong(1, id);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            String firstName = resultSet.getString(2);
            String lastName = resultSet.getString(3);
            int age = resultSet.getInt(4);
            Account.AccountStatus status = toStatus(resultSet.getString(5));
            account = new Account(firstName, lastName, age, status);
            account.setId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        return account;
    }

    @Override
    public Account update(Account account, Long id) {
        connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("UPDATE account SET first_name = ?," +
                    "last_name = ?," +
                    "age = ?," +
                    "account_status = ?" +
                    "WHERE account_id = ?");
            preparedStatement.setString(1, account.getFirstName());
            preparedStatement.setString(2, account.getLastName());
            preparedStatement.setInt(3, account.getAge());
            preparedStatement.setString(4, account.getStatus().toString());
            preparedStatement.setLong(5, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, preparedStatement);
        }
        return account;
    }

    @Override
    public void delete(Account account) {
        connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("UPDATE account SET account_status = ? WHERE first_name = ? " +
                    "AND last_name = ? " +
                    "AND age = ? " +
                    "AND account_status = ?");
            preparedStatement.setString(1, "DELETED");
            preparedStatement.setString(2, account.getFirstName());
            preparedStatement.setString(3, account.getLastName());
            preparedStatement.setInt(4, account.getAge());
            preparedStatement.setString(5, account.getStatus().toString());
            preparedStatement.executeUpdate();
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
            preparedStatement = connection.prepareStatement("UPDATE account SET account_status = ? WHERE account_id = ?");
            preparedStatement.setString(1, "DELETED");
            preparedStatement.setLong(2, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, preparedStatement);
        }

    }

    private static long findMaxId()  {
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(account_id) FROM account");
            resultSet.next();
            id = resultSet.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
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

    private static Connection getConnection(){
        try {
            connection = DriverManager.getConnection(URL);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
