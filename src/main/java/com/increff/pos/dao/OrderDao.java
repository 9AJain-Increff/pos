package com.increff.pos.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.TypedQuery;

import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

@Repository

// FIXED: 29/01/23 validate all the queries

public class OrderDao extends AbstractDao {

    // FIXED: 29/01/23 constants should be in uppercase
    private static final String SELECT_ORDER_BY_ID = "select p from OrderPojo p where id=:id";
    private static final String SELECT_ALL_ORDERS = "select p from OrderPojo p";
    private static final String GET_BETWEEN_DATE = "select p from OrderPojo p where p.createdOn BETWEEN :start and :end";

    public OrderPojo insert(OrderPojo p) {
        em().persist(p);
        return p;
    }

    public OrderPojo selectOrderById(Integer orderId) {
        TypedQuery<OrderPojo> query = getQuery(SELECT_ORDER_BY_ID, OrderPojo.class);
        query.setParameter("id", orderId);
        return getSingle(query);
    }

    public OrderPojo getOrderById(Integer id) {
        TypedQuery<OrderPojo> query = getQuery(SELECT_ORDER_BY_ID, OrderPojo.class);
        query.setParameter("id", id);
        OrderPojo o = getSingle(query);
        return o;
    }


    public List<OrderPojo> selectAll() {
        TypedQuery<OrderPojo> query = getQuery(SELECT_ALL_ORDERS, OrderPojo.class);
        return query.getResultList();
    }

    public List<OrderPojo> getOrdersForReport(LocalDateTime start, LocalDateTime end) {
        TypedQuery<OrderPojo> query = getQuery(GET_BETWEEN_DATE, OrderPojo.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }

}
