package model.services;


import model.dao.SellerDAO;
import model.entities.Seller;
import model.factory.DaoFactory;

import java.util.List;

public class SellerService {

    private final SellerDAO DAO = DaoFactory.createSellerDao();

    public List<Seller> findAll() {
        return DAO.findAll();
    }

    public void saveOrUpdate(Seller seller) {
        if (seller.getId() == null) {
            DAO.insert(seller);
        } else {
            DAO.update(seller);
        }
    }

    public void remove(Seller seller) {
        DAO.deleteById(seller.getId());
    }

}
