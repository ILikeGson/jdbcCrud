package main.java.ZhenyaShvyrkov.javacore.jdbc.repository.jdbc;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import main.java.ZhenyaShvyrkov.javacore.jdbc.model.Specialty;
import main.java.ZhenyaShvyrkov.javacore.jdbc.repository.SpecialtyRepository;

public class JdbcSpecialtyRepositoryImpl implements SpecialtyRepository {
    private static final String URL = "jdbc:postgresql://localhost:5432/crud?user=postgres&password=251084";
    private static final String DRIVER  = "org.postgresql.Driver";
    private static ResultSet result;
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement prStatement;
    private static JdbcSpecialtyRepositoryImpl jdbcSpecialtyRepository;
    private static long id;

    private JdbcSpecialtyRepositoryImpl() {}

    static {
        try {
            Class.forName(DRIVER);
            connection = getConnection();
            statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS specialty(" +
                    "specialty_id BIGINT UNIQUE NOT NULL PRIMARY KEY," +
                    "specialty_name VARCHAR(64))");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, statement);
        }
    }

    public static synchronized JdbcSpecialtyRepositoryImpl getJdbcSpecialtyRepository() {
        if (jdbcSpecialtyRepository == null) {
            jdbcSpecialtyRepository = new JdbcSpecialtyRepositoryImpl();
        }
        return jdbcSpecialtyRepository;
    }

    @Override
    public Specialty save(Specialty specialty) {
        try {
            connection = getConnection();
            id =  findMaxId();
            prStatement = connection.prepareStatement("INSERT INTO specialty" +
                    " VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            prStatement.setLong(1, ++id);
            prStatement.setString(2, specialty.getName());
            prStatement.executeUpdate();
            specialty.setId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, prStatement);
        }
        return specialty;
    }

    @Override
    public List<Specialty> read() {
        List<Specialty> specialties = new ArrayList<>();
        connection = getConnection();
        try {
            statement = connection.createStatement();
            result = statement.executeQuery("SELECT * FROM specialty");
            while (result.next()) {
                id = result.getLong("specialty_id");
                String name = result.getString("specialty_name");
                Specialty specialty = new Specialty(name);
                specialty.setId(id);
                specialties.add(specialty);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, statement,result);
        }
        return specialties;
    }

    @Override
    public Specialty readById(Long id) {
        Specialty specialty = null;
        connection = getConnection();
        try {
            prStatement = connection.prepareStatement("SELECT specialty_name FROM specialty WHERE specialty_id = ?");
            prStatement.setLong(1, id);
            result = prStatement.executeQuery();
            if (result.next()) {
                String name = result.getString(1);
                specialty = new Specialty(name);
                specialty.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, statement, result);
        }
        return specialty;
    }

    @Override
    public Specialty update(Specialty specialty, Long id) {
        connection = getConnection();
        try {
            prStatement = connection.prepareStatement("UPDATE specialty SET specialty_name = ? WHERE specialty_id = ?");
            prStatement.setString(1, specialty.getName());
            prStatement.setLong(2, id);
            prStatement.executeUpdate();
            specialty.setId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        } {
            JdbcUtils.closeQuietly(connection, prStatement);
        }
        return specialty;
    }

    @Override
    public void delete(Specialty specialty) {
        connection = getConnection();
        try {
            prStatement = connection.prepareStatement("DELETE FROM specialty WHERE specialty_name = ?");
            prStatement.setString(1, specialty.getName());
            prStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, prStatement);
        }
    }

    @Override
    public void deleteByID(Long id) {
        connection = getConnection();
        try {
            prStatement = connection.prepareStatement("DELETE FROM specialty WHERE specialty_id = ?");
            prStatement.setLong(1, id);
            prStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeQuietly(connection, prStatement);
        }

    }
    private static long findMaxId() throws SQLException {
        statement = connection.createStatement();
        result = statement.executeQuery("SELECT MAX(specialty_id) FROM specialty");
        result.next();
        return result.getLong(1);
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
