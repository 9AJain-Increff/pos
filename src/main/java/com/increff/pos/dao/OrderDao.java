package com.increff.pos.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Todo
 * Missed follwoing points which are mentioned in the document
 * https://increff.atlassian.net/wiki/spaces/TB/pages/312377489/Java+Class+layering+and+Structure#Purpose.7
 * No checked exception should be thrown from DAO Layer
 * At the class level, one should have '@Transactional' instead of the method level
 */
// TODO: 29/01/23 validate all the queries

public class OrderDao extends AbstractDao {

    // TODO: 29/01/23 constants should be in uppercase
    // TODO: 29/01/23 remove delete
    private static String delete_order_by_id = "delete from OrderPojo p where id=:id";
    // TODO: 29/01/23 why select by barcode is there here?
    private static String select_order_by_barcode = "select p from OrderPojo p where barcode=:barcode";

    // TODO: 29/01/23 which name? is this a valid sql ?
    private static String select_order_by_name = "select p from OrderPojo p where name=:name";
    private static String select_order_by_id = "select p from OrderPojo p where id=:id";
    private static String select_all_orders = "select p from OrderPojo p";
    private static String get_between_date = "select p from OrderPojo p where p.createdOn  BETWEEN :start and :end";

    public OrderPojo insert(OrderPojo p)  {
        em().persist(p);
        return p;
    }

    public OrderPojo selectOrderById(Integer orderId) {
        TypedQuery<OrderPojo> query = getQuery(select_order_by_id, OrderPojo.class);
        query.setParameter("id", orderId);
        return getSingleBrand(query);
    }

    public OrderPojo getOrderById(Integer id) {
        TypedQuery<OrderPojo> query = getQuery(select_order_by_id, OrderPojo.class);
        query.setParameter("id", id);
        OrderPojo o = getSingleBrand(query);
        return o;
        return getSingleBrand(query);
    }

    // TODO: 29/01/23 remove
    public OrderPojo checkName(String name) {
        TypedQuery<OrderPojo> query = getQuery(select_order_by_name, OrderPojo.class);
        query.setParameter("name", name);
        return getSingleBrand(query);
    }

    // TODO: 29/01/23 what is the difference between this and getOrderById method
    public OrderPojo select(int id) {
        TypedQuery<OrderPojo> query = getQuery(select_order_by_id, OrderPojo.class);
        query.setParameter("id", id);
        // TODO: 29/01/23 why are you calling getSingleBrand?
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
