package com.increff.employee.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.service.ApiException;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryDao extends AbstractDao {

    private static String delete_barcode = "delete from InventoryPojo p where barcode=:barcode";
    private static String select_id = "select p from InventoryPojo p where id=:id";
    private static String select_all = "select p from InventoryPojo p";

    private static String select_inventory = "select p from InventoryPojo p where barcode=:barcode";
    private static String check = "select p from InventoryPojo p where barcode=:barcode";
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(InventoryPojo p) throws ApiException {

        em.persist(p);


    }

    public int delete(String barcode) {
        Query query = em.createQuery(delete_barcode);
        query.setParameter("barcode", barcode);
        return query.executeUpdate();
    }


    public Boolean checkInventoryExists(String barcode) {
        TypedQuery<InventoryPojo> query = getQuery(check, InventoryPojo.class);
        query.setParameter("barcode", barcode);

        InventoryPojo p = getSingleBrand(query);

        if(p==null){return false;}
        else{
            return true;
        }
    }
    public InventoryPojo select(String barcode) {
        TypedQuery<InventoryPojo> query = getQuery(select_inventory, InventoryPojo.class);
        query.setParameter("barcode", barcode);
        return getSingleBrand(query);
    }


    public InventoryPojo select(int id) {
        TypedQuery<InventoryPojo> query = getQuery(select_id, InventoryPojo.class);
        query.setParameter("id", id);
        return getSingleBrand(query);
    }

    public InventoryPojo select(String name, String category) {
        TypedQuery<InventoryPojo> query = getQuery(select_inventory, InventoryPojo.class);
        query.setParameter("name", name);
        query.setParameter("category", category);
        return getSingleBrand(query);
    }

    public List<InventoryPojo> selectAll() {
        TypedQuery<InventoryPojo> query = getQuery(select_all, InventoryPojo.class);

        return query.getResultList();
    }
    @Transactional
    public void update(InventoryPojo p) {

    }



}
