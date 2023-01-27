package com.increff.pos.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void addOrderItem(OrderItemPojo p) throws ApiException {

        OrderItemPojo check = dao.checkOrderItemExists(p.getProductId());
        dao.insert(p);
    }

    @Transactional
    public void delete(int id) {
        // TODO: throw ApiException
        dao.delete(id);
    }


    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemPojo> getOrderItemsById(int id) throws ApiException {
        return dao.select(id);
    }

    @Transactional
    public List<OrderItemPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn  = ApiException.class)
    public void updateOrderItem( OrderItemPojo p) throws ApiException {

        OrderItemPojo ex = getOrderItem(p.getId());
        System.out.println(p.getQuantity());

        ex.setQuantity(p.getQuantity());

    }


    public OrderItemPojo getOrderItem(int id) throws ApiException {
        OrderItemPojo p = dao.orderItem(id);

        return p;
    }



    public List<OrderItemPojo> getOrderItemByOrders(List<OrderPojo> orders) throws ApiException {
        List<OrderItemPojo> orderItems = new ArrayList<>();
        for(OrderPojo order : orders){
            List<OrderItemPojo> o = getOrderItemsById(order.getId());
            orderItems.addAll(o);
        }
        return orderItems;
    }



}
