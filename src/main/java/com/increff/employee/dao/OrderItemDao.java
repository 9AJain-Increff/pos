package com.increff.employee.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.increff.employee.pojo.OrderItemPojo;
import com.increff.employee.service.ApiException;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemDao extends AbstractDao {

    private static String delete_id = "delete from OrderItemPojo p where id=:id";
    private static String select_barcode = "select p from OrderItemPojo p where barcode=:barcode";

    private static String select_name = "select p from OrderItemPojo p where name=:name";
    private static String select_id = "select p from OrderItemPojo p where orderId=:orderId";
    private static String select_all = "select p from OrderItemPojo p";

    private static String select_orderItem = "select p from OrderItemPojo p where id=:id ";
    private static String select_orderItemByBarcode = "select p from OrderItemPojo p where orderId=:orderId and barcode=:barcode";
    private static String check = "select p from OrderItemPojo p where barcode=:barcode";
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(OrderItemPojo p) throws ApiException {
        System.out.println("ankur jain");
        em.persist(p);


    }

    public int delete(int id) {
        Query query = em.createQuery(delete_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }


    public OrderItemPojo checkOrderItemExists(String barcode) {
        TypedQuery<OrderItemPojo> query = getQuery(check, OrderItemPojo.class);
        query.setParameter("barcode",barcode);
        OrderItemPojo p = getSingleBrand(query);
        return p;
    }
    public OrderItemPojo select(String barcode) {
        System.out.println("anknanana");
        TypedQuery<OrderItemPojo> query = getQuery(select_barcode, OrderItemPojo.class);
        query.setParameter("barcode", barcode);
        return getSingleBrand(query);
    }

    public OrderItemPojo checkName(String name) {
        System.out.println("anknanana");
        TypedQuery<OrderItemPojo> query = getQuery(select_name, OrderItemPojo.class);
        query.setParameter("name", name);
        return getSingleBrand(query);
    }

    public List<OrderItemPojo> select(int orderId) {
        TypedQuery<OrderItemPojo> query = getQuery(select_id, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }


    public OrderItemPojo orderItem(int id) {
        TypedQuery<OrderItemPojo> query = getQuery(select_orderItem, OrderItemPojo.class);
        query.setParameter("id", id);
        return getSingleBrand(query);
    }

    public OrderItemPojo orderItem(int orderId, String barcode) {
        TypedQuery<OrderItemPojo> query = getQuery(select_orderItemByBarcode, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        query.setParameter("barcode", barcode);
        return getSingleBrand(query);
    }

    public List<OrderItemPojo> selectAll() {
        TypedQuery<OrderItemPojo> query = getQuery(select_all, OrderItemPojo.class);

        return query.getResultList();
    }
    @Transactional
    public void update(OrderItemPojo p) {

    }



}
