package com.increff.pos.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;

@Repository

public class OrderItemDao extends AbstractDao {
    private static final String DELETE_ORDER_ITEM_BY_ID = "delete from OrderItemPojo p where id=:id";
    private static final String SELECT_ORDER_ITEM_BY_ORDER_ID = "select p from OrderItemPojo p where orderId=:orderId";
    private static final String SELECT_ALL_ORDER_ITEMS = "select p from OrderItemPojo p";
    private static final String SELECT_ORDER_ITEM_BY_ID = "select p from OrderItemPojo p where id=:id ";


    public void insert(OrderItemPojo p) {
        em().persist(p);
    }

    public int delete(Integer id) {
        Query query = em().createQuery(DELETE_ORDER_ITEM_BY_ID);
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    public List<OrderItemPojo> select(int orderId) {
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_ORDER_ITEM_BY_ORDER_ID, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }

    public OrderItemPojo getOrderItem(int id) {
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_ORDER_ITEM_BY_ID, OrderItemPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<OrderItemPojo> selectAll() {
        TypedQuery<OrderItemPojo> query = getQuery(SELECT_ALL_ORDER_ITEMS, OrderItemPojo.class);

        return query.getResultList();
    }
}
