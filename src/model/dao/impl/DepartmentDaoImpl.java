package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.dao.DepartmentDao;
import model.entities.Department;
import model.factory.DBFactory;
import model.security.error.DbException;
import model.security.error.DbIntegrityException;

public class DepartmentDaoImpl implements DepartmentDao {

    private final Connection CONNECTION;

    public DepartmentDaoImpl(Connection connection) {
        this.CONNECTION = connection;
    }

    @Override
    public Department findById(Integer id) {
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(
                "SELECT * FROM department " +
                        "WHERE Id = ?"
        )) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return this.instantiate(resultSet);
            }

            DBFactory.closeResultSet(resultSet);

            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Department> findAll() {
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(
                "SELECT * FROM department " +
                        "ORDER BY Name"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Department> departments = new ArrayList<>();

            while (resultSet.next()) {
                departments.add(this.instantiate(resultSet));
            }

            DBFactory.closeResultSet(resultSet);

            return departments;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void insert(Department department) {
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(
                "INSERT INTO department " +
                        "(Name) " +
                        "VALUES " +
                        "(?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            preparedStatement.setString(1, department.getName());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new DbException("Unexpected error! No rows affected!");
            }

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                department.setId(id);
            }

            DBFactory.closeResultSet(resultSet);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void update(Department department) {
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(
                "UPDATE department " +
                        "SET Name = ? " +
                        "WHERE Id = ?"
        )) {
            preparedStatement.setString(1, department.getName());
            preparedStatement.setInt(2, department.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(
                "DELETE FROM department " +
                        "WHERE Id = ?"
        )) {
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbIntegrityException(e.getMessage());
        }
    }

    private Department instantiate(ResultSet resultSet) throws SQLException {
        Department department = new Department();

        department.setId(resultSet.getInt("Id"));
        department.setName(resultSet.getString("Name"));

        return department;
    }

}
