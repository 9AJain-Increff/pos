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

    private static String select_order_by_id = "select p from OrderPojo p where id=:id";
    private static String select_all_orders = "select p from OrderPojo p";
    private static String get_between_date = "select p from OrderPojo p where p.createdOn  BETWEEN :start and :end";

    public OrderPojo insert(OrderPojo p) throws ApiException {
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
