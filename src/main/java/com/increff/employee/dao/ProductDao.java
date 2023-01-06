package com.increff.employee.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.ApiException;
import org.springframework.stereotype.Repository;

import com.increff.employee.pojo.EmployeePojo;

@Repository
public class ProductDao extends AbstractDao {

    private static String delete_barcode = "delete from ProductPojo p where barcode=:barcode";
    private static String select_barcode = "select p from ProductPojo p where barcode=:barcode";

    private static String select_name = "select p from ProductPojo p where name=:name";
    private static String select_id = "select p from ProductPojo p where id=:id";
    private static String select_all = "select p from ProductPojo p";

    private static String select_product = "select p from ProductPojo p where name=:name AND category=:category";
    private static String check = "select p from ProductPojo p where barcode=:barcode";
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(ProductPojo p) throws ApiException {
        System.out.println("ankur jain");
        em.persist(p);


    }

    public int delete(String barcode) {
        Query query = em.createQuery(delete_barcode);
        query.setParameter("id", barcode);
        return query.executeUpdate();
    }


    public ProductPojo checkProductExists(String barcode) {
        TypedQuery<ProductPojo> query = getQuery(check, ProductPojo.class);
        query.setParameter("barcode",barcode);
        ProductPojo p = getSingle(query);
        return p;
    }
    public ProductPojo select(String barcode) {
        System.out.println("anknanana");
        TypedQuery<ProductPojo> query = getQuery(select_barcode, ProductPojo.class);
        query.setParameter("barcode", barcode);
        return getSingle(query);
    }

    public ProductPojo checkName(String name) {
        System.out.println("anknanana");
        TypedQuery<ProductPojo> query = getQuery(select_name, ProductPojo.class);
        query.setParameter("name", name);
        return getSingle(query);
    }

    public ProductPojo select(int id) {
        TypedQuery<ProductPojo> query = getQuery(select_id, ProductPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }


    public ProductPojo select(String name, String category) {
        TypedQuery<ProductPojo> query = getQuery(select_product, ProductPojo.class);
        query.setParameter("name", name);
        query.setParameter("category", category);
        return getSingle(query);
    }

    public List<ProductPojo> selectAll() {
        TypedQuery<ProductPojo> query = getQuery(select_all, ProductPojo.class);

        return query.getResultList();
    }
    @Transactional
    public void update(ProductPojo p) {

    }



}
