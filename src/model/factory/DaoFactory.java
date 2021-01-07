package model.factory;

import model.dao.DepartmentDao;
import model.dao.SellerDAO;
import model.dao.impl.DepartmentDaoImpl;
import model.dao.impl.SellerDaoImpl;

public class DaoFactory {

    public static SellerDAO createSellerDao() {
        return new SellerDaoImpl(DBFactory.getConnection());
    }

    public static DepartmentDao createDepartmentDao() {
        return new DepartmentDaoImpl(DBFactory.getConnection());
    }

}
