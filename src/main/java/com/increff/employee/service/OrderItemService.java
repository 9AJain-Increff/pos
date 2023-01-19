package com.increff.employee.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.employee.dao.OrderItemDao;
import com.increff.employee.pojo.OrderItemPojo;
import com.increff.employee.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.util.StringUtil;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void addOrderItem(OrderItemPojo p) throws ApiException {
        normalize(p);

        System.out.println("ankur jainnnnnnnnnnn");
        OrderItemPojo check = dao.checkOrderItemExists(p.getBarcode());

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
        normalize(p);
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

    protected static void normalize(OrderItemPojo p) {
        p.setBarcode(StringUtil.toLowerCase(p.getBarcode()));
    }


}
