package com.increff.employee.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.increff.employee.pojo.OrderPojo;
import com.increff.employee.service.ApiException;
import org.springframework.stereotype.Repository;

import com.increff.employee.pojo.EmployeePojo;

@Repository
public class OrderDao extends AbstractDao {

    private static String delete_id = "delete from OrderPojo p where id=:id";
    private static String select_barcode = "select p from OrderPojo p where barcode=:barcode";

    private static String select_name = "select p from OrderPojo p where name=:name";
    private static String select_id = "select p from OrderPojo p where id=:id";
    private static String select_all = "select p from OrderPojo p";

    private static String select_order = "select p from OrderPojo p where name=:name AND category=:category";
    private static String check = "select p from OrderPojo p where barcode=:barcode";
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public OrderPojo insert(OrderPojo p) throws ApiException {
        System.out.println("ankur jain");
        em.persist(p);
        return p;
    }

    public int delete(int id) {
        Query query = em.createQuery(delete_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }


    public OrderPojo checkOrderExists(String barcode) {
        TypedQuery<OrderPojo> query = getQuery(check, OrderPojo.class);
        query.setParameter("barcode",barcode);
        OrderPojo p = getSingle(query);
        return p;
    }
    public OrderPojo select(String barcode) {
        System.out.println("anknanana");
        TypedQuery<OrderPojo> query = getQuery(select_barcode, OrderPojo.class);
        query.setParameter("barcode", barcode);
        return getSingle(query);
    }

    public OrderPojo checkName(String name) {
        System.out.println("anknanana");
        TypedQuery<OrderPojo> query = getQuery(select_name, OrderPojo.class);
        query.setParameter("name", name);
        return getSingle(query);
    }

    public OrderPojo select(int id) {
        TypedQuery<OrderPojo> query = getQuery(select_id, OrderPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }


    public OrderPojo select(String name, String category) {
        TypedQuery<OrderPojo> query = getQuery(select_order, OrderPojo.class);
        query.setParameter("name", name);
        query.setParameter("category", category);
        return getSingle(query);
    }

    public List<OrderPojo> selectAll() {
        TypedQuery<OrderPojo> query = getQuery(select_all, OrderPojo.class);

        return query.getResultList();
    }
    @Transactional
    public void update(OrderPojo p) {

    }



}
