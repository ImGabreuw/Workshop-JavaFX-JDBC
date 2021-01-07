package model.factory;

import model.security.error.DbException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DBFactory {

    private static Connection connection = null;

    public static Connection getConnection() {
        return connection == null ? openConnection() : connection;
    }

    private static Connection openConnection() {
        try {
            Properties properties = loadProperties();
            String url = properties.getProperty("dburl");

            return DriverManager.getConnection(
                    url,
                    properties
            );
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    private static Properties loadProperties() {
        try (FileInputStream fileInputStream = new FileInputStream("db.properties")) {
            Properties props = new Properties();
            props.load(fileInputStream);
            return props;
        }
        catch (IOException e) {
            throw new DbException(e.getMessage());
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
    }

}
