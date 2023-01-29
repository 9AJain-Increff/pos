package com.increff.pos.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.increff.pos.pojo.OrderItemPojo;
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
// TODO: 29/01/23 validate all the queries and methods that should be there in this dao
public class OrderItemDao extends AbstractDao {
    private static String delete_order_item_by_id = "delete from OrderItemPojo p where id=:id";
    private static String select_order_item_by_barcode = "select p from OrderItemPojo p where barcode=:barcode";

    private static String select_order_by_name = "select p from OrderItemPojo p where name=:name";
    private static String select_order_item_by_order_id = "select p from OrderItemPojo p where orderId=:orderId";
    private static String select_all_order_items = "select p from OrderItemPojo p";

    private static String select_order_Item_by_id = "select p from OrderItemPojo p where id=:id ";
    private static String select_by_order_id_and_barcode = "select p from OrderItemPojo p where orderId=:orderId and barcode=:barcode";

    private static String get_between_date = "select p from OrderPojo p where createdOn IN BETWEEN :start and :end";

    @Transactional
    public void insert(OrderItemPojo p) throws ApiException {
        em().persist(p);
    }

    public int delete(int id) {
        Query query = em().createQuery(delete_order_item_by_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }


    public OrderItemPojo checkOrderItemExists(Integer id) {
        TypedQuery<OrderItemPojo> query = getQuery(select_order_Item_by_id, OrderItemPojo.class);
        query.setParameter("id",id);
        OrderItemPojo p = getSingleBrand(query);
        return p;
    }
    public OrderItemPojo select(String barcode) {
        TypedQuery<OrderItemPojo> query = getQuery(select_order_item_by_barcode, OrderItemPojo.class);
        query.setParameter("barcode", barcode);
        return getSingleBrand(query);
    }

    // TODO: 29/01/23 remove unused methods
    public OrderItemPojo checkName(String name) {
        System.out.println("anknanana");
        TypedQuery<OrderItemPojo> query = getQuery(select_order_by_name, OrderItemPojo.class);
        query.setParameter("name", name);
        return getSingleBrand(query);
    }

    public List<OrderItemPojo> select(int orderId) {
        TypedQuery<OrderItemPojo> query = getQuery(select_order_item_by_order_id, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }


    public OrderItemPojo orderItem(int id) {
        TypedQuery<OrderItemPojo> query = getQuery(select_order_Item_by_id, OrderItemPojo.class);
        query.setParameter("id", id);
        return getSingleBrand(query);
    }

    // TODO: 29/01/23 remove unused methods
    public OrderItemPojo orderItem(int orderId, String barcode) {
        TypedQuery<OrderItemPojo> query = getQuery(select_by_order_id_and_barcode, OrderItemPojo.class);
        query.setParameter("orderId", orderId);
        query.setParameter("barcode", barcode);
        return getSingleBrand(query);
    }

    public List<OrderItemPojo> selectAll() {
        TypedQuery<OrderItemPojo> query = getQuery(select_all_order_items, OrderItemPojo.class);

        return query.getResultList();
    }

    // TODO: 29/01/23 remove unused methods
    public List<OrderPojo> getOrdersForReport(LocalDateTime start, LocalDateTime end){

        TypedQuery<OrderPojo> query = getQuery(get_between_date, OrderPojo.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();

    }

}
