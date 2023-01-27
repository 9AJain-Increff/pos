package com.increff.pos.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao extends AbstractDao {

    private static String delete_order_by_id = "delete from OrderPojo p where id=:id";
    private static String select_order_by_barcode = "select p from OrderPojo p where barcode=:barcode";

    private static String select_order_by_name = "select p from OrderPojo p where name=:name";
    private static String select_order_by_id = "select p from OrderPojo p where id=:id";
    private static String select_all_orders = "select p from OrderPojo p";

    private static String select_order_by_name_and_category = "select p from OrderPojo p where name=:name AND category=:category";
    private static String get_between_date = "select p from OrderPojo p where p.createdOn  BETWEEN :start and :end";



    public OrderPojo insert(OrderPojo p) throws ApiException {
        em().persist(p);
        return p;
    }

    public int delete(int id) {
        Query query = em().createQuery(delete_order_by_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }


    public OrderPojo checkOrderExists(String barcode) {
        TypedQuery<OrderPojo> query = getQuery(select_order_by_barcode, OrderPojo.class);
        query.setParameter("barcode",barcode);
        OrderPojo p = getSingleBrand(query);
        return p;
    }
    public OrderPojo select(String barcode) {
        TypedQuery<OrderPojo> query = getQuery(select_order_by_barcode, OrderPojo.class);
        query.setParameter("barcode", barcode);
        return getSingleBrand(query);
    }

    public OrderPojo getOrderById(Integer id) {
        TypedQuery<OrderPojo> query = getQuery(select_order_by_id, OrderPojo.class);
        query.setParameter("id", id);
        return getSingleBrand(query);
    }

    public OrderPojo checkName(String name) {
        TypedQuery<OrderPojo> query = getQuery(select_order_by_name, OrderPojo.class);
        query.setParameter("name", name);
        return getSingleBrand(query);
    }

    public OrderPojo select(int id) {
        TypedQuery<OrderPojo> query = getQuery(select_order_by_id, OrderPojo.class);
        query.setParameter("id", id);
        return getSingleBrand(query);
    }


    public OrderPojo select(String name, String category) {
        TypedQuery<OrderPojo> query = getQuery(select_order_by_name_and_category, OrderPojo.class);
        query.setParameter("name", name);
        query.setParameter("category", category);
        return getSingleBrand(query);
    }

    public List<OrderPojo> selectAll() {
        TypedQuery<OrderPojo> query = getQuery(select_all_orders, OrderPojo.class);

        return query.getResultList();
    }

    public List<OrderPojo> getOrdersForReport(LocalDateTime start, LocalDateTime end){

        TypedQuery<OrderPojo> query = getQuery(get_between_date, OrderPojo.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();

    }


}
