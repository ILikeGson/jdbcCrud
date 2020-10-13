package main.java.ZhenyaShvyrkov.javacore.jdbc.repository.jdbc;

import java.sql.*;

public class JdbcUtils {

     static void closeQuietly(Connection connection, Statement statement) {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            //NOP
        }
    }
    static void closeQuietly(Connection connection, Statement statement, ResultSet rs) {
        try {
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            //NOP
        }
    }
    static void closeQuietly(Connection connection, Statement statement, PreparedStatement preparedStatement) {
        try {
            preparedStatement.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            //NOP
        }
    }
    static void closeQuietly(PreparedStatement statement, PreparedStatement preparedStatement) {
        try {
            preparedStatement.close();
            statement.close();
        } catch (SQLException e) {
            //NOP
        }
    }
}
