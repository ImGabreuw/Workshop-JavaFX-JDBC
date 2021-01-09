package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Statement;

import model.dao.SellerDAO;
import model.entities.Department;
import model.entities.Seller;
import model.factory.DBFactory;
import model.security.error.DbException;

public class SellerDaoImpl implements SellerDAO {

    private final Connection CONNECTION;

    public SellerDaoImpl(Connection connection) {
        this.CONNECTION = connection;
    }

    @Override
    public void insert(Seller seller) {
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(
                "INSERT INTO seller "
                        + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
                        + "VALUES "
                        + "(?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            preparedStatement.setString(1, seller.getName());
            preparedStatement.setString(2, seller.getEmail());
            preparedStatement.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
            preparedStatement.setDouble(4, seller.getBaseSalary());
            preparedStatement.setInt(5, seller.getDepartment().getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new DbException("Unexpected error! No rows affected!");
            }

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                seller.setId(id);
            }

            DBFactory.closeResultSet(resultSet);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void update(Seller seller) {
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(
                "UPDATE seller "
                        + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
                        + "WHERE Id = ?"
        )) {
            preparedStatement.setString(1, seller.getName());
            preparedStatement.setString(2, seller.getEmail());
            preparedStatement.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
            preparedStatement.setDouble(4, seller.getBaseSalary());
            preparedStatement.setInt(5, seller.getDepartment().getId());
            preparedStatement.setInt(6, seller.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(
                "DELETE FROM seller " +
                        "WHERE Id = ?"
        )) {
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public Seller findById(Integer id) {
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(
                "SELECT seller.*,department.Name as DepName "
                        + "FROM seller INNER JOIN department "
                        + "ON seller.DepartmentId = department.Id "
                        + "WHERE seller.Id = ?"
        )) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Department department = this.instantiateDepartment(resultSet);

                return this.instantiateSeller(
                        resultSet,
                        department
                );
            }

            DBFactory.closeResultSet(resultSet);

            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    private Seller instantiateSeller(ResultSet resultSet, Department department) throws SQLException {
        Seller seller = new Seller();

        seller.setId(resultSet.getInt("Id"));
        seller.setName(resultSet.getString("Name"));
        seller.setEmail(resultSet.getString("Email"));
        seller.setBaseSalary(resultSet.getDouble("BaseSalary"));
        seller.setBirthDate(new java.util.Date(resultSet.getTimestamp("BirthDate").getTime()));
        seller.setDepartment(department);

        return seller;
    }

    private Department instantiateDepartment(ResultSet resultSet) throws SQLException {
        Department department = new Department();

        department.setId(resultSet.getInt("DepartmentId"));
        department.setName(resultSet.getString("DepName"));

        return department;
    }

    @Override
    public List<Seller> findAll() {
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(
                "SELECT seller.*,department.Name as DepName "
                        + "FROM seller INNER JOIN department "
                        + "ON seller.DepartmentId = department.Id "
                        + "ORDER BY Name"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Seller> sellers = new ArrayList<>();
            Map<Integer, Department> departments = new HashMap<>();

            while (resultSet.next()) {
                Department department = departments.get(
                        resultSet.getInt("DepartmentId")
                );

                if (department == null) {
                    department = this.instantiateDepartment(resultSet);

                    departments.put(
                            resultSet.getInt("DepartmentId"),
                            department
                    );
                }

                sellers.add(
                        this.instantiateSeller(resultSet, department)
                );
            }

            DBFactory.closeResultSet(resultSet);

            return sellers;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(
                "SELECT seller.*,department.Name as DepName "
                        + "FROM seller INNER JOIN department "
                        + "ON seller.DepartmentId = department.Id "
                        + "WHERE DepartmentId = ? "
                        + "ORDER BY Name"
        )) {
            preparedStatement.setInt(1, department.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Seller> sellers = new ArrayList<>();
            Map<Integer, Department> departments = new HashMap<>();

            while (resultSet.next()) {
                Department departmentObj = departments.get(resultSet.getInt("DepartmentId"));

                if (departmentObj == null) {
                    departmentObj = this.instantiateDepartment(resultSet);

                    departments.put(
                            resultSet.getInt("DepartmentId"),
                            departmentObj
                    );
                }

                sellers.add(
                        this.instantiateSeller(resultSet, departmentObj)
                );
            }

            DBFactory.closeResultSet(resultSet);

            return sellers;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }
}
