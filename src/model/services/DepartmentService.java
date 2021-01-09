package model.services;

import model.dao.DepartmentDao;
import model.entities.Department;
import model.factory.DaoFactory;

import java.util.List;

public class DepartmentService {

    private final DepartmentDao DAO = DaoFactory.createDepartmentDao();

    public List<Department> findAll() {
        return DAO.findAll();
    }

    public void saveOrUpdate(Department department) {
        if (department.getId() == null) {
            DAO.insert(department);
        } else {
            DAO.update(department);
        }
    }

}
